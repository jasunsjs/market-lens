# MarketLens

## Description

MarketLens is an Android mobile app that monitors your stock portfolio and notifies you when meaningful price movements occur, along with an AI-generated explanation of why the change happened.

**MarketLens portfolio page:**

<img width="321" height="633" alt="image" src="https://github.com/user-attachments/assets/b13e645b-bf90-4db3-bc60-bb0826dd9e8a" />

## User Guide

Video Walkthrough: https://youtu.be/So7LqXK0Z_A

## Getting Started

**Note that MarketLens is only supported on Android mobile.**

1. Checkout the [latest release](https://github.com/jasunsjs/market-lens/releases/tag/v1.0.0) & download the APK file
2. Choose any Android emulator (you can use Android Studio)
3. Drag the APK file into your emulator and launch MarketLens

Alternatively, if you would like to clone the repository, you would need to build it and use [Android Studio](https://developer.android.com/studio) to build using Gradle and launch the app through the emulator.

## Techstack

### Tools

The entire project is based on Kotlin.

- Frontend: Jetpack Compose
- Database: PostgreSQL on Supabase
- Backend function service: Supabase Edge Functions
- Design: MVVM + Layered Architecture

### APIs

- [Finnhub API](https://finnhub.io/): current stock data
- [Massive API](https://massive.com/): past stock data
- [Google Gemini API](https://ai.google.dev/gemini-api/docs): AI analyses
- [Supabase](https://supabase.com/): database & server-side function

## Design Documents

- [Diagrams](https://github.com/jasunsjs/market-lens/wiki/Diagrams): ERD Diagram (database schema), UML Class Diagrams, and Layered Architecture
- [API and Database Reference](https://github.com/jasunsjs/market-lens/wiki/API-and-Database-Reference): complete reference page of our table design and API methods

## API Rate Limits

We are using the free tier of the Finnhub, Gemini, and Massive APIs with the following rate limits:  

- **Finnhub:** 60 requests/min - 4 or 7 requests are made every time a stock detail page in the portfolio screen is opened (7 if an AI summary generated in the past 24 hours exists in the database, 4 otherwise).  
- **Gemini:** 5 requests/min, 20 requests/day - 1 request is made every time a stock detail page in the portfolio screen is opened and has not had an AI summary generated in the past 24 hours (no request is made if one exists and was generated within 24 hours).  
- **Massive:** 5 requests/min - 1 request is made every time a stock detail page in the portfolio screen is opened for the 30 day summary.

---

Created by Jason Sun, Ryan Zhou, Anthony Tecsa, Blair Wang
