


MÄÄRITTELYDOKUMENTTI
====================
Tuomas Honkala - tuho@cs.helsinki.fi


Tietorakenteiden harjoitustyö loppukesä 2016
--------------------------------------------
Dungeonette - A Random(-ish) Dungeon Generator





### Aihe

Dungeonette on Javalla ohjelmoitava algoritmi, joka tuottaa pseudosatunnaisia luolastoja roolipeliin, jota olen koodannut vapaa-ajallani.

Luolasto koostuu useista kerroksista (luolatasoista), joiden välillä liikutaan portaikkoja pitkin. Kukin kerros (luolataso) koostuu n kappaleesta huoneita, jotka kytketään toisiinsa n-1 kappaleella käytäviä. Luolataso on siis käytänöllisesti katsoen verkko, jossa huoneet ovat solmuja ja käytävät kaaria.

Algoritmin on ensisijaisesti tarkoitus tuottaa vain luolatasojen yleinen pohjapiirustus (minkä suuruisia huoneita, kuinka pitkiä ja polveilevia käytäviä). Huoneiden yksityiskohtainen sisustaminen sisällytetään algoritmiin vain, jos siihen jää harjoitustyön lopussa aikaa.



### Vaatimuksia algoritmille 

Roolipeli, jolle Dungeonette-algoritmia koodataan, perustuu ruudukkopohjaisiin karttoihin. Tällä hetkellä yksittäisen pelialueen koko on kovakoodattu 100 x 100 x 10 ruudun (x, y, z) suuruiseksi. Dungeonetella luotavien luolastojen on määrä sopia tämän kokoluokituksen sisälle. Toisin sanoen, yksi luolasto koostuu (enimmillään) kymmenestä 100 x 100 koordinaattiruudun kokoisesta luolatasosta.

Roolipelissä seinät sijaitsevat koordinaattiruutujen "välissä", joten seinällä toisistaan erotetut huoneet voivat sijaita vierekkäisissä koordinaateissa. Esim. koordinaattien (10,10,1) ja (10,11,1) välillä voi olla seinä.

Koska roolipelini tiiligrafiikka piirretään kavaljeeriprojektiona, pelissä on mahdollista esittää useamman kerroksen korkuista arkkitehtuuria. Niinpä ei olisi ainakaan pelimoottorini kannalta laitonta, että algoritmi voisi tuottaa huoneita, joiden sama huonetila ulottuisi pystysuunnassa kerroksesta 1 kerrokseen n. 

Pelisuunnittelun nojalla luolatasojen ei ole tarkoitus olla mielivaltaisen monimutkaisia labyrintteja, joissa olisi vain yksi laillinen reitti lähdöstä maaliin (portaikosta portaikkoon). Tästä syystä algoritmin tulisi suosia lyhyitä umpikujia pitkään (usean huoneen ketjuna) jatkuvien umpikujien kustannuksella.

Algoritmin ei toisaalta tule generoida liian "avoimia" luolakerroksia, joissa kaikista huoneista pääsee kulkemaan kaikkiin naapurihuoneisiin.

Algoritmin toiminnan kannalta on ensisijaisen tärkeää, että 

1. kaikkiin huoneisiin löytyy ainakin jotakin kautta laillinen kulkureitti
2. luolatason lähtökoordinaatti (portaikko josta saavuttiin) sijaitsee mahdollisimman kaukana luolatason poistumiskoordinaatista (portaikko joka vie seuraavaan luolatasoon)
3. algoritmi huomioi jatkuvuuden luolatasojen välillä: luolatasoja yhdistävän portaikon on sijaittava molemmissa kerroksissa samassa x, y -koordinaatissa.
4. kahden käytävän leikatessa koordinaatistossa toisensa algoritmi osaa käsitellä leikkauspisteen risteyksenä
5. luolaston "satunnaisuus" on mahdollista sitoa siemenlukuun, jolloin täsmälleen identtinen luolasto voidaan tarvittaessa generoida uudelleen
6. luolatasot ("pohjapiirustukset") esitetään Javan konsoliprinttauksena

Algoritmin toiminnan kannalta on toissijaisen tärkeää, että

1. huoneet voivat olla erikokoisia ja -muotoisia
2. huoneita yhdistävien käytävien leveys voi vaihdella
3. kahta huonetta yhdistävä käytävä ei ole automaattisesti suora lyhin reitti huoneesta toiseen, vaan käytävä voi mutkitella ja kenties jopa haarautua pieniksi umpikujiksi
4. huoneisiin voidaan generoida myös lukittuja ovia ja niihin sopivia avaimia, jotka generoidaan toisaalle (esim. avain huoneeseen #7 löytyy huoneesta #3)
5. luolakerrosten välillä on selvää rakenteellista variaatiota (huoneiden lukumäärä ja koko, käytävien lukumäärä ja pituus)


### Käytettäviä algoritmeja ja tietorakenteita

Koska luola-algoritmini on vielä hahmottelu- ja proof-of-concept-vaiheessa, en pysty vielä aukottomasti luettelemaan mitä kaikkia algoritmeja tulen käyttämään toteutuksessani. 

Laillinen kulku kaikkiin huoneisiin tarkistetaan luultavasti jonkinlaisella leveys- tai syvyyssuuntaisella läpikäynnillä (BFS-/DFS-algoritmi tai niiden muunnelma). Verkon syklittömyys ei ole sinänsä mikään arvo luolaston pelattavuuden kannalta, joten syklittömyyden tarkistaminen ei liene aiheellista.

Huoneesta toiseen johtavat käytävät ("verkon kaaret") hyödyntävät A*star-reitinhakua ainakin silloin, jos käytävän on kierrettävä jokin kolmas huone, johon kyseisen käytävän ei kuulu johtaa. 

Haluaisin tutkia minkälaisia mahdollisuuksia Game of Life -algoritmilla (tai sen muunnelmilla) olisi huoneiden ja käytävien muodon rikkomisessa. En pitäisi erityisen toivottavana, että kaikki luolastoihin generoitavat huoneet olisivat neliön tai suorakaiteen muotoisia.

Luolasto tallennetaan char tai int [][][] -tyyppiseen matriisiin. Generoinnin aikana hyödynnetään luultavasti lukuisia saman kokoisia apumatriiseja.



### Annettavat syötteet

Algoritmi ottaa vastaan seuraavat syötteet:

+ siemenluku, joka määrittää kohtien 2), 3), 4) ja 5) kanssa raudanlujasti minkälainen luolasto algoritmista tuotetaan ulos. 
+ luolatasojen lukumäärä (1-10)
+ huonetiheys (suhdeluku, 1-100; mitä suurempi, sitä enemmän huoneita per kerros)
+ käytävätiheys (suhdeluku 1-100; mitä suurempi, sitä enemmän käytäviä huoneesta toiseen)
+ arkkitehtuurin volatiliteetti (suhdeluku 1-100; mitä suurempi, sitä enemmän huone- ja käytävätiheys vaihtelee samassa luolastossa luolatasosta toiseen)

Suorituskykyanalyysin kannalta algoritmille voisi olla mahdollista antaa myös seuraava syöte:

+ luolatason korkeus ja leveys (jos muu kuin 100 x 100 ruutua)



### Tavoitteena olevat aika- ja tilavaativuudet

Koska kyse on algoritmista, jota ajetaan tosiaikaisesti pelin ollessa käynnissä, ei liene suotavaa, että luolaston generointi johtaisi pelaajan kannalta kohtuuttoman pitkään odotukseen.

Alustavan arvioni mukaan algoritmin aika- ja tilavaativuus lienee vähintään O(k*(n^2)) missä k viittaa kerrosten lukumäärään ja n viittaa luolatasojen suuruusluokkaan.


### Lähteet

Harjoitustyö perustunee pääosin syksyn 2015 Tietorakenteet ja algoritmit -kurssin kurssimateriaaleihin.
