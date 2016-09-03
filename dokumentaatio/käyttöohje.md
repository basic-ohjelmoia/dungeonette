Käyttöohje: Dungeonette
=======================

Dungeonette on ajettavissa perusasetuksillaan suoraan komentoriviltä käynnistämällä dist-kansiosta löytyvä jar-paketti komennolla
_java -jar Dungeonette.jar_

Ohjelman ajaminen tuottaa samaan kansioon satunnaisgeneroitua luolastoa esittävän tekstitiedoston _dungeon.txt_.

Generoitavan luolaston rakenteeseen voi vaikuttaa komentoriville kirjoitettavilla käynnistysparametreilla. Listan laillisista käynnistysparametreista saa näkyviin ajamalla ohjelman komennolla _java -jar Dungeonette.jar -help_. Ohessa yhteenveto käynnistysparametreista ja niiden käyttötarkoituksesta. Huomaa, että mikään käynnistysparametri ei ole pakollinen. Syöttämättä jätetyt parametrit käsitellään oletusarvojen mukaisesti. 

Käynnistysparametrit
--------------------

### -x <10...1000>
Määrittää kuinka leveä luolaston kukin kerros voi olla. Syöte annetaan kymmeninä koordinaatteina, joten _-x 10_ tarkoittaisi 100 koordinaattia leveää kerrosta. Hyvä arvo tälle voisi olla 20. Arvo 200 näyttää kauas zoomattuna tietokonetaiteelta.

### -y <10...1000>
Määrittää kuinka korkea luolaston kukin kerros voi olla. Syöte annetaan kymmeninä koordinaatteina, joten _-y 50_ tarkoittaisi 500 koordinaattia korkeaa kerrosta. Hyvä arvo tälle voisi olla 20. Arvo 200 näyttää kauas zoomattuna tietokonetaiteelta.

### -z <1...10000>
Määrittää kuinka monta kerrosta luolaan generoidaan. Satojen tai tuhansien kerrosten generointi ei ole suositeltavaa, ellei tarkoituksena sitten ole tehdä jotain massiivista suorituskykytestiä.

### -density <5...100>
Määrittää kuinka tiheästi kuhunkin kerrokseen asetellaan huoneita. Arvolla 100 huoneet generoidaan niin lähelle toisiaan kuin mahdollista. Jos luolan kerrokset generoidaan kovin pieninä, density-arvoa ei kannata asettaa liian matalaksi. -x 20 -y 20 luolalle pienin suositeltava density-arvo voisi olla 20 tai 25. -x 50 -y 50 kokoiselle luolalle density-arvon 10 pitäisi olla ihan turvallinen.

### -interconnectivity <1...100>
Määrittää kuinka paljon ylimääräisiä huoneita yhdistäviä käytäviä luolaan generoidaan. Arvolla 1 niitä ei generoida juuri lainkaan. Arvolla 100 niitä generoidaan paljon. Vaikutukset luolan rakenteeseen voivat olla aika arvaamattomia, varsinkin pienelle luolalle. 

### -deadendiness <1...100>
Toiminnaltaan hyvin samankaltainen kuin -interconnectivity, mutta deadendiness-käytävät päättyvät (yleensä) umpikujina. Jos haluat generoida luolan jossa huoneiden rakenne pirstaloituu, kokeile arvoa _-deadendiness 100_

### -straightness <50...100>
Vaikuttaa käytävien suoruuteen. Mitä matalampi arvo, sitä todennäköisemmin käytävät mutkittelevat. Arvo 100 ei takaa 100 % mutkattomia käytäviä!

### -supersize
Tämä käynnistysparametri pyrkii maksimoimaan suurten huoneiden lukumäärän luolassa. Huomaa kuitenkin, että matalat -density -arvot haittaavat suurten huoneiden generoimista.

### -long
Pyrkii erottamaan huoneita toisistaan mahdollisimman pitkillä käytävillä. Long-parametrin kanssa -densityn kattoarvo on 15.

### volatile
Pyrkii tekemään kerrosten välille merkittäviä kokoeroja (käytännössä säädellen huonelukua). Vaikutuksen pitäisi näkyä selvimmillään suhteellisen pienillä luolilla.

### -speedtest
Tämän parametrin kanssa luola generoidaan pelkkänä nopeustestinä ilman tiedoston kirjoittamista.

### -random
Tämän parametrin kanssa luola generoidaan satunnaisella siemenluvulla.

### -seed <siemensana>
Tämän parametrin avulla luolagenerointiprosessille voidaan määrittää vapaavalintainen siemen. Käyttö: _-seed tähänsiemen_. Huomaa, että jos -random ja -seed -parametrit jätetään käyttämättä, luola generoidaan oletussiemenellä, joka on aina sama.


Ääriarvojen käytöstä
--------------------
Riippuen käytössä olevasta laitteistosta Dungeonetten toiminta ei kestä kaikkia ääriarvoja. Luolaston generointi suuruusluokassa _-x 1000, -y 1000_ lienee aika teoreettista. Omalla koneellani suurin generoimani luola oli -x 500, y 500. 

Osa käynnistysparametreista on luonteeltaan ristiriidassa keskenään ja tietyt parametri- ja arvoyhdistelmät saattavat aiheuttaa ristiriitoja ohjelman toiminnassa. _-density_ -parametrin kanssa kannattaa olla erityisen varovainen: arvo 5 tarkoittaa, että vain reilu 5 % kaikesta lattiapinta-alasta on luolageneroinnin kannalta laillista!
