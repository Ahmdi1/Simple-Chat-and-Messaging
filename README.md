# Simple-Chat-and-Messaging
SCD Project Simple Chat And Messaging
Simple Chat / Messaging Simulator
README File
Project Overview

This project is a desktop-based Chat / Messaging Simulator built in Java (Swing).
It demonstrates the use of five core software design patterns:

Factory Pattern – For message creation

Builder Pattern – For chat session configuration

Decorator Pattern – For styling messages with timestamps

Observer Pattern – For live message updates

Singleton Pattern – For a single shared ChatEngine

The application simulates a simple chat interface where the user can send messages, receive fake incoming messages, and see timestamps applied through decorators.

Features

Desktop GUI using Java Swing

Send text and system messages

Auto-generated simulated messages

Optional timestamp decorator

Multiple design patterns combined in one application

Dark and light theme support (through session builder)

Design Pattern Usage
1. Factory Pattern – MessageFactory

Used to create:

TextMessage

SystemMessage
This removes the need for manually calling constructors.

2. Builder Pattern – ChatSessionBuilder

Used to build:

Username

Theme
Ensures clean and flexible session configuration.

3. Decorator Pattern – TimestampDecorator

Adds timestamps without changing the original Message class.

4. Observer Pattern – ChatObserver

The GUI updates immediately when new messages are broadcast.

5. Singleton Pattern – ChatEngine

Only one instance exists and handles:

Message sending

Observer updates

Chat log routing

How to Run the Project

Install Java (JDK 8 or above).

Compile the project:

javac Ahmed.java


Run the application:

java Ahmed

Folder Structure
/project
│── Ahmed.java
│── README.txt
│── Screenshots/   (optional)
│── Report/        (optional)

Technologies Used

Java

Swing (GUI)

Object-Oriented Programming

Design Patterns

Author

Syed Ahmed Kaleem
Cmsid: 63518
BS Software Engineering – 5th Semester
