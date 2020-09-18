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
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.constant.Role;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.exception.PortfolioExistException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    public PortfolioController(PortfolioService portfolioService, UserService userService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> createNewPortfolio(@PathVariable String username, @RequestBody PortfolioDTO portfolioDTO) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Throw exception if user has already created an eportfolio
        if (portfolioService.findByUsername(username).isPresent()) {
            throw new PortfolioExistException(username);
        }
        portfolioService.create(user, portfolioService.fromPortfolioDTO(portfolioDTO));
        return new SuccessResponse<>().toOk();
    }

    // Find portfolio by username
    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> findByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        return new SuccessResponse<>("portfolio", result).toOk();
    }


    @GetMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<DBObject>> findContentByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        return new SuccessResponse<>("content", result.getContent()).toOk();
    }


    @PutMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> uploadContent(@PathVariable String username, @RequestBody JsonNode jsonPayload) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        HashMap<String, Object> map = mapper.convertValue(jsonPayload, typeRef);

        portfolioService.updateContent(result, map);
        return new SuccessResponse<>().toOk();
    }

    @DeleteMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> deleteContent(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        Portfolio resultContent = portfolioService.deleteContent(result);
        return new SuccessResponse<>("content", resultContent).toOk();
    }

    // Search portfolio with pagination
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Object>> search(@RequestParam @NotEmpty String query, @RequestParam int page, @RequestParam int size) {

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
}
