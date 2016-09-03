Toteutusdokumentti: Dungeonette
===============================

Yleistä
-------
Dungeonette on Javalla koodattu satunnaisluolageneraattori roolipelille, jota olen kehittänyt vapaa-ajallani. Roolipelin ympäristöt ovat kolmiulotteisia tiilikarttoja. Yksi "kartta" vastaa yhtä luonnollista sijaintia pelin maailmassa, esimerkiksi kaupunkia, linnaa tai luolastoa. Karttojen mittasuhteille - leveydelle, pituudelle ja korkeudelle (x,y,z) - ei ole periaatteessa ylärajaa, mutta mitä luultavimmin haluan pitää ne suurin piirtein suuruusluokkavälillä 100 x 100 x 10...200 x 200 x 10. 

Luolien vaatimuksista
---------------------
Dungeonette on optimoitu suurin piirtein edellä mainittua suuruusluokkaa vastaavien monikerroksisten satunnaisluolien tuottamiseen. Pelisuunnittelun kannalta optimaalinen luola olisi rakenteeltaan melko selkeä, eikä sisältäisi kohtuutonta määrää umpikujamaisia rakenteita. Kunkin kerroksen sisääntulopisteen (portaikko) ja ulospääsypisteen (toinen portaikko) ei tulisi sijaita liian lähellä toisiaan, mutta portaikkojen keskinäisen etäisyyden ei silti tarvitse vastata kerroksen absoluuttista maksimietäisyyttä.

Koska pelini ympäristöt tallennetaan kolmiulotteiseen matriisiin, luolan kerrosten välillä on ylläpidettävä yhdenmukaista koordinaatistoa. Kerroksen n portaikko koordinaatissa (50, 50, n) tulee jatkoa kerroksessa n+1 niin ikään koordinaatissa (50, 50, n+1). Minkään kerroksen mikään huone ei voi sijaita samalle ympäristölle määriteltyjen yhteisten ulkorajojen ulkopuolella. 

Luolat generoidaan siemenluvun pohjalta. Sama siemenluku tuottaa samoilla parametreilla täysin identtisen luolan. Käyttäjäystävällisyyden nimissä siemenluvun voi syöttää ohjelmalle vapaavalintaisena sanana, joka sitten muunnetaan siemenluvuksi.

Luolageneroinnin eteneminen
---------------------------
Dungeonetten luolagenerointi perustuu huoneisiin. Huoneet generoidaan karkeaan koordinaatistoon, joka vastaa pääkoordinaatistossa 10 x 10 tiilen (ruudun/koordinaatin) kokoista aluetta. Huone voi karkean koordinaattinsa sisällä olla periaatteessa minkä muotoinen tahansa. Huone voi toisaalta levittäytyä useamman kuin yhden karkean koordinaatin alueelle, suurimmillaan 3 x 3 karkeaa koordinaattia eli 30 x 30 tiiltä vastaavalle alueelle. Oli huone minkä suuruinen tahansa, kukin karkea koordinaatti voi kuulua vain yhdelle huoneelle kerrallaan. Samaan kerrokseen ei siis voi generoida toisiaan leikkaavia huoneita. 

Erilliset huoneet voivat silti siinä mielessä sulautua yhteen, että huoneiden generointia seuraava käytävien kaivertamisvaihe saattaa hävittää huoneesta ulkoseiniä. Mitä useampi käytävä generoidaan, sitä suurempi deformaatio huoneisiin voi kohdistua. Käytävien kaivertaminen on kuitenkin algoritmisella tasolla tiukasti säänneltyä, joten ellei algoritmia eksplisiittisesti parametrisoida tuottamaan poikkeuksellisen paljon käytäviä, huoneiden pitäisi erottua luolakerroksen pohjapiirustuksesta (konsolitulosteesta) selkeästi.

Dungeonette säilöö generoitavan luolan perusparametrit Specification-nimiseen luokkaan ja itse luolaston Environment-olioksi, jonka jokainen kerros muodostaa Floor-olion ja kerroksen jokainen huone Room-olion. Varsinainen luolagenerointi tapahtuu Generator-pakkauksen staattisissa luokissa, joista Architect ajaa varsinaista luolageneraation pääsilmukkaa. 

Luolan generointi tapahtuu pääpiirteissään siten, että Architect valitsee yhden huoneen aktiiviseksi ja yrittää löytää sen vierestä sopivaa tyhjää tilaa naapurihuoneelle. Jos ja kun tällainen tila löytyy (validointi tapahtuu RoomInserter-luokassa), uusi huone generoidaan ja se ja aktiivisena ollut huone merkitään toisiinsa kytkettäviksi.

Architect-luokan pääsilmukan keskeisin tietorakenne on RoomQueue-pseudojono, johon kukin onnistuneesti generoitu (laillisen paikan karkeasta koordinaatistosta löytänyt) huone lisätään. RoomQueue eroaa aidosta jonosta siinä, että dequeue-kutsu ei automaattisesti poista huonetta jonosta, vaan ainoastaan vähentää huoneelle myönnettyjä jatkoyhteyksiä yhdellä yksiköllä. Jatkoyhteydellä tarkoitetaan käytävää seuraavaan, vielä generoimattomaan huoneeseen. Vasta kun dequeue toteaa huoneen jatkoyhteysarvon nollaksi, kutsu poistaa huoneen jonosta. Jonosta poistettua huonetta ei enää tämän jälkeen käsitellä Architechin pääsilmukassa.

Kun Architect havaitsee RoomQueuensa tyhjäksi luolakerros katsotaan valmiiksi. Kerros voi siis valmistua ennen kuin sen nimellinen maksimihuonemäärä (esim. 50 kpl) on saavutettu. Huoneiden generoinnin jälkeen generoidaan vielä käytävät huoneiden välille. Vaikka algoritmi tukee myös mielivaltaisten käytävien generointia, suurin osa generoitavista käytävistä perustuu huoneiden välille merkittyihin eksplisiittisiin yhteyksiin. Algoritmi pyrkii välttämään tarpeetonta huoneiden yhdistelyä: algoritmin näkökulmasta riittää, että huoneella on edes yksi laillinen yhteys luolaston aloituspisteeseen (portaikkoon).

Kartan eheyden kannalta Dungeonette lähtee olettamuksesta, että PassageCarver (käytävien kaivertaja) toimii AINA virheettömästi ja onnistuu yhdistämään kaikki keskenään yhdistettäväksi määritetyt huoneet laillisesti. Laiton yhdistäminen tapahtuisi tässä tapauksessa kartan rajojen ulkopuolelta. Havahduin implementaationi potentiaalisiin heikkouksiin vasta aivan projektin loppuhetkillä, mikä pakotti minut aluksi turvautumaan melko rujoihin varmistuksiin koodissani. Projektin viimeisellä viikolla sain onneksi hiottua kaiverrusrutiinia hieman elegantimmaksi. Toiminta vaikuttaisi nyt virheettömältä. 

Aika- ja tilavaatimukset
------------------------
Kuten tekemäni mittaukset näyttäisivät osoittavan, Dungeonetten aika- ja tilavaativuuden pitäisi olla O(n) eli täysin linjassa Specification-luokalle annettujen syötteiden suuruusluokkaan. Teoreettisen suurilla syötteillä aikavaativuudessa aikavaativuus saattaa painua jopa selvästi O(n):n alle, riippuen siitä sisällytetäänkö generoitavien huoneiden lukumäärä osaksi syötettä vai ei. Huoneluku ei nimittäin kaikilla parametrisoinneilla skaalaudu ylöspäin syötteen mukana. Huonemäärää toisaalta rajoittaa sekin, ettei huoneita voi generoida enempää kuin mitä kerroksen lattiapinta-alaan mahtuu.

![alt text](https://github.com/basic-ohjelmoia/dungeonette/blob/master/dokumentaatio/kuvat/luolagenerointi.png)
> Luolan generointiin kuluva aika kasvaa suunnilleen lineaarisesti syötteen (luolan koon) kasvaessa. Mittatulokset ovat millisekuntteja. 10M ruudun luola on 1000 x 1000 koordinaattia x 10 kerrosta.

Kuvan tulokset saatiin seuraavilla komentorivisyötteillä:

> java -jar Dungeonette.jar -x 100 -y 50 -z 10 -density 15 -speedtest

> java -jar Dungeonette.jar -x 100 -y 100 -z 10 -density 15 -speedtest

> java -jar Dungeonette.jar -x 200 -y 100 -z 10 -density 15 -speedtest

> java -jar Dungeonette.jar -x 200 -y 200 -z 10 -density 15 -speedtest

Suorituskykyanalyysi
--------------------
Kaikkine luolagenerointia valvovine aputauluineen Dungeonette osoittautui varsin muistisyöpöksi. Viidennen viikon implementaatiossani Dungeonettella ei ollut mahdollista generoida juurikaan 2000 x 2000 koordinaattia x 10 luolakerrosta suurempia luolastoja. Sitä suuremmilla syötteillä generaatio päättyy Javan virtuaalikoneen kekovirheeseen.

Onnistuin lievittämään muistipaineita poistamalla Environment-luokasta kaksi tarpeetonta kolmiulotteista matriisia, joihin oli tarkoitus säilöä sama tieto luolaston rakenteesta, joka on jo poimittavissa Floor[] -tietorakenteesta. Tämän muutoksen jälkeen Dungeonette generoi jopa 5000 x 5000 koordinaattia x 10 luolakerrosta kokoisia ympäristöjä. Ilahduttavana sivuvaikutuksena kolmiulotteisen matriisin poistaminen suurin piirtein puolitti luolagenerointiin kuluvan ajan!

Sen jälkeen kun olin siivonnut koodistani ylettömän määrän System.out.print -käskyjä, minun oli aluksia vaikea keksiä muita keinoja, joilla luolageneroinnin kestoa olisi saanut olennaisesti nopeutettua. Poistamalla Dungeonettesta esimerkiksi rutiinin, joka yhdistää rinnakkaisia käytäviä alkoveiksi, nopeutti luolagenerointia vain muutamilla prosenteilla. Se ei tuntunut mielekkäältä parannukselta, koska hintana oli luolien esteettisten arvojen heikentyminen. Vastaava muutaman prosentin nopeuslisä oli lopulta saavutettavissa sillä, että muutin luolageneroinnin suunnan määrittelyn täysin satunnaisesta painotetun satunnaiseksi (tarkoittaen, että hakupää välttelee kerrosten äärilaitoja). 

Dungeonette tallentaa luolansa tekstitiedostoksi lähinnä demonstratiivisessa mielessä, tallennusmuodolla kun ei ole mitään tekemistä pelini tiilikarttatiedostojen kanssa. Oli kuitenkin mielenkiintoista huomata kuinka paljon kirjoitusnopeuteen pystyi vaikuttamaan sillä, että "torttua tortun päälle" kirjoittava String-merkkijono korvattiin StringBuilderilla. 2000 x 2000 x 10 -kokoisen luolan kohdalla kirjoitusnopeus lyheni 21 sekunnista 2,4 sekuntiin!

![alt text](https://github.com/basic-ohjelmoia/dungeonette/blob/master/dokumentaatio/kuvat/kirjoitusnopeus.png)
> Luolatiedoston kirjoitusnopeus parani huikaisevan paljon, kun luolaa esittävät merkkijonot rakennetaan StringBuilderilla.

Kuvan tulokset saatiin seuraavilla komentorivisyötteillä:

> java -jar Dungeonette.jar -x 100 -y 100 -z 10 -density 15

> java -jar Dungeonette.jar -x 200 -y 200 -z 10 -density 15 
