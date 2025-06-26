# SubNet_Mask_Translator
Subnet Mask Converter 🔄

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen)](CONTRIBUTING.md)

A lightweight Java utility for converting subnet masks between **CIDR, dotted-decimal (octet), and binary formats** with validation. Perfect for networking students, developers, and network engineers.

## Features ✨
- **Three-way conversion**:
  - CIDR (e.g., `/24`) ↔ Octet (e.g., `255.255.255.0`) ↔ Binary (e.g., `11111111.11111111.11111111.00000000`)
- **Input validation** (rejects invalid formats like `/33` or `256.0.0.0`)
- **Reusable API** - Reprocess inputs without recreating objects
- **Clean OOP design** (enums, modular methods, bitwise operations)

## Installation 📦
1. **Requires Java 17+**
2. **Using as a library**:
   - Add the `.java` file to your project.
3. **Command-line**:
   ```bash
   javac SubNetMaskTranslate.java
