package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.PortfolioExistException;
import tech.eportfolio.server.exception.PortfolioNotFoundException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
    }

    @PostMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public Portfolio createNewPortfolio(@PathVariable String username, @RequestBody Portfolio portfolio) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Throw exception if user has already created an eportfolio
        if (portfolioService.findByUsername(username).isPresent()) {
            throw new PortfolioExistException(username);
        }
        // Set attributes for eportfolio
        Portfolio toCreate = new Portfolio();
        toCreate.setContent(portfolio.getContent());
        toCreate.setDescription(portfolio.getDescription());
        toCreate.setTitle(portfolio.getTitle());
        toCreate.setVisibility(portfolio.getVisibility());
        toCreate.setUsername(user.getUsername());
        return portfolioService.save(toCreate);
    }

    // Find eportfolio by username
    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public Portfolio findByUsername(@PathVariable String username) {
        return portfolioService.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
    }

    // Search eportfolio with pagination
    @GetMapping("/search")
    public Page<Portfolio> search(@RequestParam @NotEmpty String query, @RequestParam int page, @RequestParam int size) {
        return portfolioService.searchWithPagination(query, PageRequest.of(page, size));
    }
}
