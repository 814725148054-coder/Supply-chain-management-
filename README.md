# Supply Chain Management System

A menu-driven Java console application that manages suppliers, products, purchase orders, stock and deliveries. Data is saved to text files so records persist between runs.

Built as a Java Programming (CS25C09) project at SRM TRP Engineering College — supports **SDG 12: Responsible Consumption and Production**.

## Features
- Add and list suppliers
- Add products with price, opening stock and reorder level
- Create purchase orders (validates supplier/product, calculates total cost, status `PENDING`)
- Record deliveries (stock updates automatically, order becomes `DELIVERED`)
- Stock report with **LOW STOCK** warning
- File-based persistence (`suppliers.txt`, `products.txt`, `orders.txt`, `deliveries.txt`)

## OOP concepts used
Classes & objects, encapsulation, interface (`Storable`), collections (`ArrayList`), exception handling, file handling.

## Requirements
JDK 8 or above

## Run
```bash
javac SupplyChain.java
java SupplyChain
```

## Authors
- Gousigan S
- Sri Hari Sarveswar K
- Subuelamaran P
