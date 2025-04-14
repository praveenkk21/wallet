Microservices Wallet System

This project is a microservices-based system built with Java Spring Boot and various technologies like Kafka, MySQL, Spring Data JPA, Hibernate, and Simple Mail Sender. The system includes multiple services interacting through Kafka for communication, with JWT authentication managed by a central WalletGateway service.

Services Overview:

1. WalletGateway (8086)
  Purpose: This service handles the authorization of JWT bearer tokens for all incoming API requests. It acts as a gateway for all other services, ensuring that only authorized requests are processed.

  Responsibilities:

  Authorization of JWT tokens

  Routing of requests to appropriate services

  Exposes a few open API endpoints

2. UserService (8082)
  Purpose: Handles user-related operations such as adding, updating, deleting user data, and uploading user images.

  Responsibilities:

  UserController: Manages user data and image uploads.

  AuthController: Manages user authentication and login.

  GitHubController: Manages OAuth2 GitHub registration.

  Kafka Producer: Sends messages to userUpdatedTopic when a user is created or updated. Triggers wallet creation in the WalletService.

3. WalletService (8083)
  Purpose: Manages user wallets, including wallet creation and checking balance.

  Responsibilities:

  WalletController: Manages wallet creation and balance checking.

  Kafka Consumer: Listens for user creation messages from the UserService and automatically creates a wallet with a default balance of 100 INR for new users.

  Transaction Consumer: Listens for transactionInitiated messages and updates the wallets for both users involved in a transaction.

4. TxnService (8081)
  Purpose: Handles transactions between users, checking wallet balances and updating them accordingly.

  Responsibilities:

  TransactionController: Handles the logic for transferring money between user wallets.

  Kafka Consumer: Listens for transaction initiation events and processes the wallet updates.

5. NotificationService (8084)
  Purpose: Sends email notifications to users.

  Responsibilities:

  Sends email notifications to users (using a mail trap site with the email: admin@wallet.com).

6. RouterService (8762)
  Purpose: Acts as a Eureka registry for all services, allowing dynamic discovery of services by other services in the system.

  Technologies Used
  Java Spring Boot: The core framework for building the microservices.

  Spring Data JPA and Hibernate: ORM tools for database interaction.

  Kafka: For asynchronous communication between microservices.

  MySQL: Used for data storage.

  JWT: For token-based user authentication.

  Simple Mail Sender: For sending emails to users.



  Service Interactions

  UserService produces a Kafka message to userUpdatedTopic whenever a user is created or updated. This triggers the WalletService to create a wallet with a default balance.

  WalletService listens to the userUpdatedTopic and creates a wallet for the new user with a default balance of 100 INR. It also listens to the transactionInitiated topic to handle wallet updates during transactions.

  TxnService listens for transaction initiation events and processes wallet transfers between users.

  NotificationService sends email notifications to users regarding transaction events and wallet updates.
