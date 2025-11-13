# Contributing to FX Deals Warehouse

Thank you for your interest in contributing to the FX Deals Warehouse project! This document provides guidelines and instructions for contributing.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

Please be respectful and constructive in all interactions. We aim to maintain a welcoming and inclusive environment for all contributors.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/FX-deals.git`
3. Create a feature branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Test thoroughly
6. Commit with clear messages
7. Push to your fork
8. Submit a pull request

## Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8+ (or use the included Maven wrapper)
- Docker & Docker Compose (optional, for MySQL deployment)
- IDE with Spring Boot support (IntelliJ IDEA, Eclipse, or VS Code)

### Local Development
```bash
# Clone the repository
git clone https://github.com/abdelkouddousalami/FX-deals.git
cd FX-deals

# Run with H2 in-memory database
./mvnw spring-boot:run

# Or with Docker and MySQL
docker-compose up -d
```

### Running Tests
```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw clean test jacoco:report

# View coverage report at: target/site/jacoco/index.html
```

## Coding Standards

### Java Code Style
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)
- Add Javadoc comments for public methods and classes
- Use Lombok annotations to reduce boilerplate code

### Architecture Guidelines
- Follow the layered architecture: Controller → Service → Repository
- Keep controllers thin - business logic belongs in services
- Use DTOs for API requests/responses
- Validate input at the controller level using Bean Validation
- Handle exceptions with the global exception handler

### Code Example
```java
/**
 * Service method description.
 *
 * @param request the request object
 * @return the response object
 * @throws CustomException if validation fails
 */
@Transactional
public ResponseDTO processRequest(@Valid RequestDTO request) {
    log.info("Processing request: {}", request.getId());
    
    // Business logic here
    
    return mapToResponse(result);
}
```

## Testing Guidelines

### Test Coverage Requirements
- Minimum 80% code coverage for new features
- Write unit tests for all service methods
- Write integration tests for API endpoints
- Test both success and failure scenarios
- Test edge cases and boundary conditions

### Test Structure
```java
@Test
@DisplayName("Should successfully import valid FX deal")
void shouldImportValidDeal() {
    // Given
    FxDealRequest request = createValidRequest();
    
    // When
    FxDealResponse response = service.importDeal(request);
    
    // Then
    assertThat(response).isNotNull();
    assertThat(response.getDealUniqueId()).isEqualTo(request.getDealUniqueId());
}
```

### Test Naming Convention
- Use descriptive test method names
- Format: `should[ExpectedBehavior]When[Condition]`
- Example: `shouldThrowExceptionWhenDealIsDuplicate`

## Commit Guidelines

### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, no logic change)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

### Examples
```
feat(controller): add endpoint to export deals as CSV

Implemented new GET endpoint /api/fx-deals/export that exports
all deals in CSV format with pagination support.

Closes #123
```

```
fix(service): correct currency validation logic

Fixed issue where invalid currency codes were accepted due to
incorrect exception handling.

Fixes #456
```

## Pull Request Process

### Before Submitting
1. Ensure all tests pass: `./mvnw test`
2. Verify code compiles without warnings: `./mvnw clean compile`
3. Run code coverage: `./mvnw clean test jacoco:report`
4. Update documentation if needed
5. Add tests for new features
6. Follow the coding standards

### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe the tests you ran and their results

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests pass
- [ ] No new warnings
```

### Review Process
1. Submit your PR with a clear description
2. Ensure CI/CD checks pass
3. Address reviewer feedback promptly
4. Keep PR focused on a single feature/fix
5. Squash commits if requested

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Java Coding Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- [REST API Best Practices](https://restfulapi.net/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## Questions?

If you have questions or need help, please:
- Open an issue for bugs or feature requests
- Check existing issues and discussions
- Reach out to the maintainers

Thank you for contributing! 🎉
