Testausdokumentti: Dungeonette
==============================

Dungeonetten testaus perustuu JUnit-testeihin. Testikattavuudessa tähdätään siihen, että suurin piirtein jokaisen luokan jokainen metodi tulee vähintään kertaalleen haastettua testipatterin puitteissa. Ihan jokaiselle luokalle ei ole kirjoitettu omia erillisiä JUnit-testejä, sillä joitakin luokkia olisi melko epäkäytännöllistä testata muista erillään.

Koska satunnaisluolagenerointiin liittyy nimensä mukaisesti vahva satunnaisuuden elementti, Dungeonetten koodiin on kätketty joitakin debug-henkisiä piirteitä, jotka aktivoidaan testauksen toistettavuuden varmistamiseksi.

Koska Dungeonetten tehtänä on tuottaa mielekkäällä tavalla tutkittavia satunnaisluolia, JUnit-testeillä pyritään ensisijaisesti varmistamaan, että luolatuotos on laillinen. Hyvä esimerkki laittomasta tuotoksesta voisi olla esimerkiksi satunnaisgeneroitu huone, jolla ei olisi lainkaan lattiapinta-alaa. On myönnettävä, että luolan kokonaisrakenteen laillisuuden varmistamisessa testaus on jossain määrin puutteellista. Testauksen laadun parantamiseksi tältä osin voisin harkita esimerkiksi saman tyyppistä fill-tarkistusta kuin aikaisemmin vertaisarvioimassani Dungeongen-harjoitustyössä.

Dungeonettea varten luodun RoomQueue-tietorakenteen virheettömän toiminnan varmistaminen on niin ikään testaamisen keskiössä. Jos RoomQueuen toiminta menisi rikki, luolagenerointiprosessi ei voisi selvitä virheestä.

### Suorituskykytestauksesta

Dungeonetten suorituskykytestaus on jakaantuu luolan generointiin ja luolaa esittävät tekstitiedoston kirjoittamiseen kuluvan ajan mittaamiseen.

Dungeonette käynnistää luolageneraatiota koskevan suorituskykyajastimen kun Environment-luolasäiliö konstruktoidaan ja pysäyttää sen kun koko luolasto on generoitu, mutta tiedoston kirjoittamista (tai luolan printtaamista) ei ole vielä aloitettu. Suorituskyvyn mittaamista haittaavat ylimääräiset System.out.print-käskyt on kuitenkin pyritty eliminoimaan koodista, joten mittauksen pitäisi toimia varsin laadukkaasti.

Jos luola kirjoitetaan tiedostoksi (komentorivikäskyllä -speedtest tiedostoa ei kirjoiteta), kirjoittamiseen kuluva ajastin käynnistyy välittömästi tiedostonkirjoittajan alustamisen yhteydessä. Ajanotto päättyy, kun tiedosto on kirjoitettu.

Millisekunneissa ilmoitettavat suoritusajat ilmoitetaan Dungeonetten antaman tulosteen lopussa.

