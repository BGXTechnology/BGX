## BGX MOBILE  KOWABUNGA!– PURE BLOCKCHAIN APPS
 
 This repository is a separate BGX subproject aimed at demonstrating the network – mobile app at work. In the first phase of its development in 2018 (KOWABUNGA! Version), we developed an iOS and Android application that can demonstrate the BGX network’s capabilities of conducting digital commerce. You can get an idea of the application interface through the Pinter-est(https://www.pinterest.ca/pin/322218548339016971/ ) service.

   BGX is a blockchain of a new generation, aimed at supporting ecosystems that work on top of a single decentralized network with a special F-BFT (Federated Fault Tolerance) Consensus. Individual businesses and organizations can interact with the general customer base (B2B2C model and exchange value, represented in terms of crypto-currency tokens. The current version of the BGX Network is built using Sawtooth. Although it can support various transaction families, this application demon-strates only one of the network’s uses – the purchase of various goods and services made by individu-als using tokens produced by the nodes. 

## APPS FEATURES
   The main paradigm of the application is a network wallet represented by virtual payment cards, as well as an e-commerce system built on top of the Magento platform (Open source License - OSL V 3.0). The application is built on top of the API integration layer, which in turn refers to the BGX API and the Magento API. This approach allows a separate site to register users and store their profiles without the need of sharing that information with any other participants in the ecosystem. Within the network, the user's global identifier is his cryptographic address (public key playing the role of a virtu-al payment card number).

   In this application, the end user can:
   
-	Open, export and import payment cards for two currencies (in this version, DEC and BGT to-kens are presented for the demonstration);
-	Exchange tokens among themselves (transfers from card to card);
-	Buy digital products and download them from the catalog

    A node holder/business (an organization within an ecosystem built on top of BGX) can:
    
    -	Configure the application (White Label — change color, logos and other visual attributes using special codes);
    -	To issue virtual cards with the possibility of their replenishment through the standard app store mechanisms;
    -	Manage a catalog of digital products, including all the capabilities of the Magento platform.

    Although the application is built for demonstration purposes, its working version is presented in the app stores:
   
   - Google Play (Android) version is available at  https://play.google.com/store/apps/details?id=com.bgx.test (download is limited to the test version only);
   - The App Store (iOS) version is limited to the TestFlight application (you need to download the Tet Flight application https://itunes.apple.com/ca/app/testflight/id899247664?mt=8  to use it) and register an e-mail at partnership@bgx.ai .

## APP CUSTOMIZATION (WHITE LABEL)
   The application is constructed in such a way as to give the possibility of managing the design. To do this, at the welcome Screen, you should long press on the logo, which leads to the appearance of an additional window for entering the seven-digit code that changes the style of the application, motivation phrase, logo and language:

a.	Code: 0000000
-	<color>#ffffff</color>
-	<logo>uploads/interface/icon_logo_color_dark.png</logo>
-	<language>en</language>
-	<text>The place where people can come to find and discover anything they might want to buy online</text>
b.	Code: 1010000
-	<color>#000000</color>
-	<logo>uploads/interface/icon_logo_color_white.png</logo>
-	<language>en</language>
-	<text>The place where people can come to find and discover anything they might want to buy online</text>
c.	Code: 1010101
-	<color>#000000</color>
-	<logo>uploads/interface/icon_logo_color_white.png</logo>
-	<language>ru</language>
-	<text>The place where people can come to find and discover anything they might want to buy online</text>
d.	Code: 0000101
-	<color>#ffffff</color>
-	<logo>uploads/interface/icon_logo_color_dark.png</logo>
-	<language>ru</language>
-	<text>The place where people can come to find and discover anything they might want to buy online</text>
 
## APPS ARCHITECTURE & TECHNOLOGY
   Applications are built in accordance with accepted standards, as hybrid applications based on the iONIC framework. The development was carried out in collaboration with the company TITAN TECHNOLOGY (https://www.titancorpvn.com), a software outsourcing service. (http://intelligentobjects.io/digitalxpert/index.html)
 
 **The Architecture Layers: **
  MOBILE APPLICATIONS (Android, iOS)
  IONIC , AngularJS
  CORDOVA, FIREBASE
  
  *INTEGRATION API*
  BGX API - MAGENTP eCommerce
  
  **NODE CORE**                          -------   **ANOTHER NODE**
  Intel Sawtooth, Grapf Database (DAG)
  
  
## USING BGX MOBILE APPS
   Any organization can use mobile applications under the GPLv3 license. While the core and API layer are published under the APACHE 2.0 license, applications are a separate part of the project that can be developed and distributed regardless of the BGX protocol.
BGX Technology is interested in partnering with other developers to develop blockchain and AI technologies. 
  We see our mission as helping the entire developer community to form a critical mass of practical applications in this field of innovation. Not only did we form a crypto-fund to support in-dependent teams and individual developers, but we are open to branching out the project based on its first version of BGX KOWABUNGA! Enjoy!
