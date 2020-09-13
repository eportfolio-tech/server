package tech.eportfolio.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.exception.PortfolioExistException;
import tech.eportfolio.server.exception.PortfolioNotFoundException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;
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
    public ResponseEntity<SuccessResponse<Portfolio>> createNewPortfolio(@PathVariable String username, @RequestBody Portfolio portfolio) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Throw exception if user has already created an eportfolio
        if (portfolioService.findByUsername(username).isPresent()) {
            throw new PortfolioExistException(username);
        }
        // Set attributes for eportfolio
        Portfolio toCreate = new Portfolio();
        toCreate.setDescription(portfolio.getDescription());
        toCreate.setTitle(portfolio.getTitle());
        toCreate.setVisibility(portfolio.getVisibility());
        toCreate.setUsername(user.getUsername());
        return new SuccessResponse<>("portfolio", portfolioService.save(toCreate)).toOk();
    }

    // Find portfolio by username
    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Portfolio>> findByUsername(@PathVariable String username) {
        Portfolio result = portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
        return new SuccessResponse<>("portfolio", result).toOk();

    }

    // Search portfolio with pagination
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Object>> search(@RequestParam @NotEmpty String query, @RequestParam int page, @RequestParam int size) {
        Page<Portfolio> result = portfolioService.searchWithPagination(query, PageRequest.of(page, size));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        return new SuccessResponse<>(map).toOk();
    }
}
