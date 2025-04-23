# Book Social Network

A social platform for book enthusiasts to share, manage, and borrow books from other users.

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

## Setup and Installation

1. Clone the repository
2. Start the required services using Docker:
   ```bash
   docker-compose up -d
   ```
3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Features

- Book management (add, update, archive)
- Book sharing system
- Book borrowing functionality
- Feedback and rating system
- User authentication
- Book cover image upload

## API Endpoints

### Books

- `POST /books/save` - Add a new book
- `GET /books/{book-id}` - Get book details
- `GET /books/getBooks` - Get paginated list of books
- `GET /books/owner` - Get user's books
- `GET /books/borrowed` - Get borrowed books
- `GET /books/returned` - Get returned books
- `PATCH /books/shareable/{book-id}` - Update book's shareable status
- `PATCH /books/archive/{book-id}` - Archive/unarchive book
- `POST /books/borrow/{book-id}` - Borrow a book
- `PATCH /books/borrow/returned/{book-id}` - Mark book as returned
- `PATCH /books/borrow/return/approved/{book-id}` - Approve book return
- `POST /books/book-cover` - Upload book cover

## Database

PostgreSQL database is used for data storage with the following configuration:

- Database Name: book_social_network
- Port: 5432

## Email Service

MailDev is used for development email testing:

- SMTP Port: 1025
- Web Interface Port: 3080
