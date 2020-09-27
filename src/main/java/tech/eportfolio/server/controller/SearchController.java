package tech.eportfolio.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.exception.TagNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/search")
public class SearchController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final TagService tagService;

    private final UserTagService userTagService;

    private final ObjectMapper objectMapper;

    public SearchController(PortfolioService portfolioService, UserService userService, TagService tagService, UserTagService userTagService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.objectMapper = objectMapper;
    }

    // Search portfolio with pagination
    @GetMapping("/keyword")
    public ResponseEntity<SuccessResponse<Object>> searchByKeyword(@RequestParam @Valid @NotBlank String query, @RequestParam int page, @RequestParam int size) {

        List<Visibility> searchVisibilities = addVisibilities();

        Page<Portfolio> result = portfolioService.searchByKeywordWithPaginationAndVisibilities(query, PageRequest.of(page, size), searchVisibilities);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        return new SuccessResponse<>(map).toOk();
    }

    /**
     * Return user's tags
     *
     * @param tagName username
     * @return User
     */
    @GetMapping("/tag")
    public ResponseEntity<SuccessResponse<Object>> searchByTag(@RequestParam @Valid @NotBlank String tagName, @RequestParam int page, @RequestParam int size) {

        List<Visibility> searchVisibilities = addVisibilities();

        Tag tag = tagService.findByName(tagName).orElseThrow(() -> (new TagNotFoundException(tagName)));
        List<UserTag> userTags = userTagService.findByTagId(tag.getId());
        // Extract all userIds from userTags
        List<String> userIds = userTags.stream().map(UserTag::getUserId).collect(Collectors.toList());
//        List<Portfolio> portfolios = portfolioService.findByUserIdIn(userTags.stream().map(UserTag::getUserId).collect(Collectors.toList()));
        Page<Portfolio> result = portfolioService.searchByTagWithPaginationAndVisibilities(PageRequest.of(page, size), searchVisibilities, userIds);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        return new SuccessResponse<>(map).toOk();
    }


    // functional methods to add visibilities
    private List<Visibility> addVisibilities() {
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

        return searchVisibilities;
    }
}
