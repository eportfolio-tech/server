package tech.eportfolio.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.exception.PortfolioExistException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.TagNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.common.utility.NullAwareBeanUtilsBean;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.TagService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.UserTagService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final TagService tagService;

    private final UserTagService userTagService;

    private final ObjectMapper objectMapper;

    public static final String CONTENT = "content";

    public static final String PORTFOLIO = "portfolio";

    public PortfolioController(PortfolioService portfolioService, UserService userService, TagService tagService, UserTagService userTagService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> createNewPortfolio(@PathVariable String username, @RequestBody PortfolioDTO portfolioDTO) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Throw exception if user has already created an eportfolio
        if (portfolioService.findByUsername(username).isPresent()) {
            throw new PortfolioExistException(username);
        }
        Portfolio portfolio = portfolioService.create(user, portfolioService.fromPortfolioDTO(portfolioDTO));
        return new SuccessResponse<>(PORTFOLIO, portfolio).toOk();
    }

    // Find portfolio by username
    @GetMapping("/{username}")
//    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Map<String, Object>>> findByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        User user = userService.findByUsername(result.getUsername()).orElseThrow(() -> new UserNotFoundException(username));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("avatarUrl", user.getAvatarUrl());
        return new SuccessResponse<>(PORTFOLIO, map).toOk();
    }

    @PatchMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> updatePortfolio(@PathVariable String username, @RequestBody PortfolioDTO update) {
        Portfolio portfolio = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        NullAwareBeanUtilsBean.copyProperties(update, portfolio);
        Portfolio result = portfolioService.save(portfolio);
        return new SuccessResponse<>(PORTFOLIO, result).toOk();
    }


    @GetMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<DBObject>> findContentByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        return new SuccessResponse<>(CONTENT, result.getContent()).toOk();
    }


    @PutMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<DBObject>> uploadContent(@PathVariable String username, @RequestBody JsonNode jsonPayload) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        HashMap<String, Object> map = mapper.convertValue(jsonPayload, typeRef);

        portfolioService.updateContent(result, map);
        return new SuccessResponse<>(CONTENT, result.getContent()).toOk();
    }

    @DeleteMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> deleteContent(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        Portfolio resultContent = portfolioService.deleteContent(result);
        return new SuccessResponse<>(CONTENT, resultContent).toOk();
    }

    // Search portfolio with pagination
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Object>> search(@RequestParam @Valid @NotBlank String query, @RequestParam int page, @RequestParam int size) {

        List<Visibility> searchVisibilities = new LinkedList<>();
        // Anyone should be able to search public portfiolio
        searchVisibilities.add(Visibility.PUBLIC);
        // Retrieve authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if user has login, get its role from database
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException(authentication.getName()));
            // Being a user gives him access to portfolio that requires UNVERIFIED_USER visibility
            searchVisibilities.add(Visibility.UNVERIFIED_USER);
            // If the user's account has been verified, grant access to portfolio that requires VERIFIED_USER visibility
            if (StringUtils.equals(user.getRoles(), Role.ROLE_VERIFIED_USER.name())) {
                searchVisibilities.add(Visibility.VERIFIED_USER);
            }
        }

        Page<Portfolio> result = portfolioService.searchWithPaginationAndVisibilities(query, PageRequest.of(page, size), searchVisibilities);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        return new SuccessResponse<>(map).toOk();
    }

    /**
     * Return user's tags
     *
     * @param username username
     * @return User
     */
    @GetMapping("/search-by-tag")
//    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<Portfolio>>> findPortfoliosByTag(@RequestParam String tagId) {
        Tag tag = tagService.findById(tagId).orElseThrow(() -> (new TagNotFoundException(tagId)));
        List<UserTag> userTags = userTagService.findByTagId(tag.getId());
        List<Portfolio> portfolios = portfolioService.findByUserIdIn(userTags.stream().map(UserTag::getUserId).collect(Collectors.toList()));
        return new SuccessResponse<>("portfolio", portfolios).toOk();
    }
}
