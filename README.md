Namnsdagar
==========

Namnsdagar en Android-app som påminner dig om dina kompisars namnsdagar.

Användning
-------
Vid start väljer du om du vill använda dig av den officiella listan över namnsdagar eller den inoficiella.
Välj vilka kontakter du vill få påminnelser, pilla lite med inställningarna sedan är det bara att luta sig tillbaka!

Om
-------
Den här appen är ett resultat av [Sweddits hackaton](http://www.reddit.com/r/sweden/comments/18muv2/nu_k%C3%B6r_vi_ig%C3%A5ng_med_androidhackaton/) som skapades efter initiativ av [SwedishDude](http://www.reddit.com/user/SwedishDude).
Mer information om projektet och vilka som deltagit finns [här](https://docs.google.com/document/d/1zNbrrferMtFvOqqw06S5g2zd5wIOL2j_vvvEyAUZheo/edit#).

Bygg-instruktioner
-------
Se till att ha en ANDROID_HOME variabel som pekar på din android-sdk.

Instruktionerna nedan antar att du står i samma mapp som build.xml.
För en release-build:  
$ ant release  
För att sedan installera applikationen så krävs det att APK:n signeras. Intruktioner för ur du signerar en APK hittar du [här](http://developer.android.com/tools/publishing/app-signing.html).

För en debug-build:  
$ ant debug  
$ ant installd # för att installera debug-builden.

Licens
-------
Svenska Namnsdagar är licenserad enligt [GPLv3](http://www.gnu.org/licenses/gpl.txt)
och använder 'android.jar' och 'android-support-v4.jar' enligt [APACHE License 2](http://www.apache.org/licenses/LICENSE-2.0.txt)
