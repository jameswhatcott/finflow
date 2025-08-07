# FinFlow

A comprehensive personal finance management application built with Spring Boot, featuring budget tracking, transaction management, and financial analytics.

## 🚀 Features

### Core Functionality
- **Dashboard Overview**: View total balance, monthly income/expenses, and net worth changes
- **Budget Management**: Create and manage monthly budgets with income and expense categories
- **Transaction Tracking**: Add, view, and manage financial transactions
- **Category Management**: Organize transactions with custom income and expense categories
- **Account Management**: Track multiple bank accounts and their balances
- **Monthly Analytics**: View financial summaries by month with filtering capabilities

### User Experience
- **Modern UI**: Clean, responsive design built with Tailwind CSS
- **Inline Editing**: Edit budget categories and planned amounts directly in the interface
- **Real-time Updates**: Dynamic updates without page refreshes
- **Authentication**: Secure user registration and login system
- **Mobile Responsive**: Works seamlessly on desktop and mobile devices

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: H2 (in-memory for development)
- **Frontend**: Thymeleaf templates with Tailwind CSS
- **Security**: Spring Security with form-based authentication
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

Before running this application, ensure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- Git (for cloning the repository)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/jameswhatcott/finflow.git
cd finflow
```

### 2. Build the Application
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

### 4. Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

## 📖 Usage Guide

### Initial Setup

1. **Register a New Account**
   - Visit `http://localhost:8080/register`
   - Fill in your details (name, email, username, password)
   - Click "Register"

2. **Login to Your Account**
   - Use your registered credentials to log in
   - You'll be redirected to the dashboard

### Dashboard Overview

The dashboard provides a comprehensive view of your financial status:

- **Total Balance**: Sum of all account balances
- **Monthly Income**: Total income for the current month
- **Monthly Expenses**: Total expenses for the current month
- **Net Worth Change**: Difference between income and expenses
- **Recent Transactions**: Latest transaction history

### Budget Management

1. **Access Budget Dashboard**
   - Click "Budget" in the navigation bar
   - View your current month's budget overview

2. **Manage Categories**
   - **Add Categories**: Click the "+" button to add new income or expense categories
   - **Edit Categories**: Click the pencil icon to edit category names and planned amounts
   - **Delete Categories**: Click the trash icon to remove categories
   - **Save Changes**: Click the save icon to persist your changes

3. **Set Planned Amounts**
   - Click on any "Planned" cell to edit the amount
   - Changes are automatically saved when you click the save button

### Transaction Management

1. **View Transactions**
   - Click "Transactions" in the navigation bar
   - View all transactions for the current month
   - Use the month filter to view different months

2. **Add New Transactions**
   - Fill out the "Add New Transaction" form
   - Required fields: Amount, Description, Date, Type, Account
   - Optional: Category assignment
   - Click "Add Transaction" to save

3. **Transaction Details**
   - View transaction history in a clean table format
   - See transaction amounts, categories, and accounts
   - Filter by month to track spending patterns

### Account Management

1. **View Accounts**
   - Click "Accounts" in the navigation bar
   - See all your linked accounts and their balances

2. **Add Accounts**
   - Use the account creation form
   - Specify account type (Checking, Savings, Credit, Investment)
   - Set initial balance and account details

### Category Management

1. **View Categories**
   - Click "Categories" in the navigation bar
   - See all your income and expense categories

2. **Create Categories**
   - Add new categories with type (Income/Expense)
   - Assign colors and descriptions for better organization

## 🔧 Configuration

### Application Properties

The application uses default H2 in-memory database for development. Key configuration files:

- `src/main/resources/application.properties` - Main application configuration
- `src/main/resources/application-dev.properties` - Development-specific settings

### Database Configuration

For production deployment, update the database configuration in `application.properties`:

```properties
# Example for PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 🚀 Deployment

### Deploy to Render

1. **Create a Render Account**
   - Sign up at [render.com](https://render.com)

2. **Connect Your Repository**
   - Connect your GitHub repository to Render
   - Choose "Web Service" as the service type

3. **Configure the Service**
   - **Build Command**: `mvn clean compile`
   - **Start Command**: `mvn spring-boot:run`
   - **Environment**: Java 17

4. **Environment Variables**
   Add these environment variables in Render:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SERVER_PORT=8080
   ```

5. **Database Setup**
   - Create a PostgreSQL database in Render
   - Update the database URL in your application properties
   - Set the database credentials as environment variables

### Deploy to Heroku

1. **Install Heroku CLI**
   ```bash
   # macOS
   brew install heroku/brew/heroku
   
   # Windows
   # Download from https://devcenter.heroku.com/articles/heroku-cli
   ```

2. **Create Heroku App**
   ```bash
   heroku create your-app-name
   ```

3. **Add PostgreSQL**
   ```bash
   heroku addons:create heroku-postgresql:mini
   ```

4. **Deploy**
   ```bash
   git push heroku main
   ```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### API Testing
Use tools like Insomnia or Postman to test the REST API endpoints:

#### User Management
```bash
# Create User
POST /api/users
{
  "name": "John Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "password": "password123"
}

# Get User
GET /api/users/{id}
```

#### Transaction Management
```bash
# Create Transaction
POST /api/transactions
{
  "amount": 100.00,
  "description": "Grocery shopping",
  "transactionDate": "2024-01-15",
  "transactionType": "EXPENSE",
  "categoryId": 1,
  "accountId": 1
}

# Get Transactions
GET /api/transactions
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/jameswhatcott/finance/personal_finance_tracker/
│   │       ├── controller/          # REST and Web controllers
│   │       ├── entity/             # JPA entities
│   │       ├── repository/         # Data access layer
│   │       ├── service/            # Business logic
│   │       ├── config/             # Configuration classes
│   │       └── FinFlowApplication.java
│   └── resources/
│       ├── static/                 # CSS, JS, images
│       │   └── images/            # Logo and assets
│       ├── templates/              # Thymeleaf templates
│       └── application.properties  # Configuration
└── test/                          # Test files
```

## 🔒 Security Features

- **Spring Security**: Form-based authentication
- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session handling
- **CSRF Protection**: Cross-site request forgery protection

## 🎨 UI/UX Features

- **Tailwind CSS**: Utility-first CSS framework
- **Font Awesome**: Icon library for better visual experience
- **Responsive Design**: Mobile-first approach
- **Modern Components**: Clean, professional interface

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues:

1. Check the application logs for error messages
2. Ensure all prerequisites are installed
3. Verify database connectivity (for production deployments)
4. Create an issue in the GitHub repository

## 🔮 Future Enhancements

- [ ] Plaid integration for automatic bank transaction import
- [ ] Export functionality (PDF reports, CSV data)
- [ ] Advanced analytics and charts
- [ ] Multi-currency support
- [ ] Recurring transaction setup
- [ ] Financial goal tracking
- [ ] Mobile app development

## 📊 Performance

- **Database**: Optimized queries with proper indexing
- **Caching**: Spring Boot's built-in caching mechanisms
- **Frontend**: Optimized CSS and JavaScript loading
- **Security**: Efficient authentication and authorization

---

**Built with ❤️ using Spring Boot and Tailwind CSS**

*FinFlow - Where your money flows freely* 🚀 