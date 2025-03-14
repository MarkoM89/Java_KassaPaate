# Java kassapääte

## 1. Mistä on kyse?

Javalla tehty kassapäätesovellus, joka on samanlainen kuin toinen tekemäni projekti, jossa on Python-ohjelmoinkieli käytössä. Ohjelmalla pyrin tekemään sovellusken mikä voisi käyttää kassapäätteissä esimerkiksi ruokakaupassa.
Ohjelmalle syötetään tuotteita ja, kun on valmis, siirrytään maksuosioon.

Ohjelma tallentaa kuitit, kun ostokset on tehty.

Tuotteet, kuitit sekä maksukortit ovat MariaDb-tietokannassa.

## 2. Rajoitukset

Ohjelma on rajoitettu tuotteiden käsittelyyn ja ostamiseen vaikka kaupalla voisi myös esimerkiksi olla kaupan henkilöstön hallinta.

Tuotteet syötetään käsin verrattuna, että kassapäätteessä olisi viivakoodinlukija.

Pankki tietokanta lähinnä esittää pankin tietokantaa, johon otettaisiin yheyttä maksun aikana, jos käytetään maksukortteja.

## 3. Tietokannan rakenne

...

## 3. Ohjelman käyttöönotto

### 3.1 Tietokannan asennus

Ohjelma käyttää MariaDB-tietokantajärjestelmää, pääohjelmassa syötetään alussa tietokannasta osat tiedoista, jotka ovat käyttäjän nimi, salasana, verkkokone / ip-osoite, portti sekä tietokannan nimi:

        Properties connConfig = new Properties();
        connConfig.setProperty("user", "käyttäjä");
        connConfig.setProperty("password", "salasana");
        
Kun, tietokantaan otetaan yhteyttä, syötetään seuraavaan käskyt loput tiedot, verkkokone / ip-osoite, portti sekä tietokannan nimi:

		(Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/kokeilutietokanta", connConfig)
		
connConfig on Properties-muuttujan nimi, johon syötetään käyttäjän nimi ja salasana

### 3.2 Taulukkojen luonti ja esiluotujen tietojen syöttö

CREATE TABLE pankki(
tunniste INT PRIMARY KEY AUTO_INCREMENT,
nimi VARCHAR(30) NOT NULL,
saldo DECIMAL(12,4) NOT NULL
);

INSERT INTO pankki (nimi, saldo)
VALUES
('Mikko', 200.00),
('Noora', 200.00),
('Tapio', 200.00),
('Otto', 200.00);

CREATE TABLE tuote(
tuotetunniste INT PRIMARY KEY AUTO_INCREMENT,
tuotenimi VARCHAR(30) NOT NULL,
yksikköhinta DECIMAL(6,2) NOT NULL
);

INSERT INTO tuote (tuotenimi, yksikköhinta)
VALUES
('Kurkku', 0.48),
('Tomaatti', 1.29),
('Ananas', 2.72),
('Omena', 0.41);

CREATE TABLE kuitti(
kuittitunnus INT PRIMARY KEY AUTO_INCREMENT,
osto_aika TIMESTAMP NOT NULL,
kokonaishinta DECIMAL(8,4) NOT NULL
);

CREATE TABLE ostettu_tuote(
tunnus INT PRIMARY KEY AUTO_INCREMENT,
kuittitunnus INT NOT NULL,
tuotetunnus INT NOT NULL,
tuotemäärä INT NOT NULL,
FOREIGN KEY (kuittitunnus) REFERENCES kuitti(kuittitunnus),
FOREIGN KEY (tuotetunnus) REFERENCES tuote(tuotetunniste)
);
