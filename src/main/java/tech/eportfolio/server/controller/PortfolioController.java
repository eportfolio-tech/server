package tech.eportfolio.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.PortfolioExistException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    public static final String CONTENT = "content";

    public static final String PORTFOLIO = "portfolio";

    @Autowired
    public PortfolioController(@Qualifier("PortfolioServiceCacheImpl") PortfolioService portfolioService, @Qualifier("UserServiceCacheImpl") UserService userService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    // Find portfolio by username
    @GetMapping("/{ownerUsername}")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> findByUsername(@PathVariable String ownerUsername) {
        Portfolio result = portfolioService.foundPortfolioByUsername(ownerUsername);
        User user = userService.findByUsername(result.getUsername()).orElseThrow(() -> new UserNotFoundException(ownerUsername));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("avatarUrl", user.getAvatarUrl());
        return new SuccessResponse<>(PORTFOLIO, map).toOk();
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

    @PatchMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> updatePortfolio(@PathVariable String username, @RequestBody PortfolioDTO update) {
        Portfolio portfolio = portfolioService.foundPortfolioByUsername(username);
        Portfolio result = portfolioService.updatePortfolio(portfolio, update);
        return new SuccessResponse<>(PORTFOLIO, result).toOk();
    }


    @GetMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<DBObject>> findContentByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.foundPortfolioByUsername(username);
        return new SuccessResponse<>(CONTENT, result.getContent()).toOk();
    }


    @PutMapping("/{username}/content")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<DBObject>> uploadContent(@PathVariable String username, @RequestBody JsonNode jsonPayload) {
        Portfolio result = portfolioService.foundPortfolioByUsername(username);

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
        Portfolio result = portfolioService.foundPortfolioByUsername(username);
        Portfolio resultContent = portfolioService.deleteContent(result);
        return new SuccessResponse<>(CONTENT, resultContent).toOk();
    }

}
