# JavaFX-AES-Encryption-Tool

## 📌 Opis projektu

**JavaFX-AES-Encryption-Tool** to aplikacja napisana w języku **Java**, która umożliwia szyfrowanie i deszyfrowanie danych z wykorzystaniem algorytmu **AES (Advanced Encryption Standard)**. Aplikacja posiada interfejs graficzny stworzony przy użyciu biblioteki **JavaFX**, co zapewnia intuicyjną obsługę dla użytkownika.

## 🛠 Wymagania

Aby uruchomić projekt, potrzebujesz:

- **Java Development Kit (JDK) 11** lub nowszy
- **Maven** do zarządzania zależnościami i budowania projektu

## 🚀 Instalacja i uruchomienie

1. **Klonowanie repozytorium:**

   ```bash
   git clone https://github.com/MatiLUzak/AES.git
   cd AES
   ```

2. **Budowanie projektu za pomocą Mavena:**

   ```bash
   mvn clean install
   ```

3. **Uruchomienie aplikacji:**

   ```bash
   mvn javafx:run
   ```

   *Upewnij się, że w pliku `pom.xml` masz skonfigurowany plugin `javafx-maven-plugin`.*

## 📂 Struktura projektu

```
AES/
├── .idea/
├── Model/
│   ├── AES.java
│   └── ...
├── view/
│   ├── Main.java
│   ├── AESController.java
│   ├── AESView.fxml
│   └── ...
├── .gitignore
├── pom.xml
└── README.md
```

- **Model/** – zawiera implementację logiki szyfrowania AES, np. plik `AES.java`.
- **view/** – zawiera pliki związane z interfejsem użytkownika, w tym kontroler `AESController.java` i plik FXML `AESView.fxml`.
- **.gitignore** – plik określający, które pliki i katalogi mają być ignorowane przez Git.
- **pom.xml** – plik konfiguracyjny Mavena, definiujący zależności i pluginy.

## ✍️ Autor

- **MatiLUzak** – [Profil GitHub](https://github.com/MatiLUzak)

## 📜 Licencja

Ten projekt jest licencjonowany na podstawie licencji MIT. Szczegóły znajdują się w pliku `LICENSE`.
