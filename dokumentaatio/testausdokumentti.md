Testausdokumentti: Dungeonette
==============================
Tämä dokumentti on "work-in-progress"
-------------------------------------

Dungeonetten testaus perustuu JUnit-testeihin. Testikattavuudessa tähdätään siihen, että suurin piirtein jokaisen luokan jokainen metodi tulee vähintään kertaalleen haastettua testipatterin puitteissa. Ihan jokaiselle luokalle ei ole kirjoitettu omia erillisiä JUnit-testejä, sillä joitakin luokkia olisi melko epäkäytännöllistä testata muista erillään.

Koska satunnaisluolagenerointiin liittyy nimensä mukaisesti vahva satunnaisuuden elementti, Dungeonetten koodiin on kätketty joitakin debug-henkisiä piirteitä, jotka aktivoidaan testauksen toistettavuuden varmistamiseksi.

Koska Dungeonetten tehtänä on tuottaa mielekkäällä tavalla tutkittavia satunnaisluolia, JUnit-testeillä pyritään ensisijaisesti varmistamaan, että luolatuotos on laillinen. Hyvä esimerkki laittomasta tuotoksesta voisi olla esimerkiksi satunnaisgeneroitu huone, jolla ei olisi lainkaan lattiapinta-alaa. 

Dungeonettea varten luodun RoomQueue-tietorakenteen virheettömän toiminnan varmistaminen on niin ikään testaamisen keskiössä. Jos RoomQueuen toiminta menisi rikki, luolagenerointiprosessi ei voisi selvitä virheestä.

### Suorituskykytestauksesta

Dungeonetten suorituskykytestaus on toistaiseksi varsin alkeellista. Dungeonette käynnistää suorituskykyajastimen kun Environment-luolasäiliö konstruktoidaan ja pysäyttää sen kun koko luolasto on generoitu, mutta sen printtaamista ei vielä aloitettu. Suorituskyvyn mittaamista haittaa, että koodissa on yhä debug-henkisiä system.print.out-käskyjä. Konsoliin tulostaminen syö Dungeonetten suorituskykyä ratkaisevasti.

Millisekunneissa ilmoitettavaa luolageneroinnin suoritusaikaa voi hakea konsolitulosteesta hakusanalla "Aikaa meni: ".

