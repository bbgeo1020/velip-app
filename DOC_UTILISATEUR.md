# Guide Utilisateur — VelibApp

Bienvenue sur **VelibApp**, votre application de cartographie et de suivi en temps réel des stations Vélib de la Métropole Parisienne.

## 🚀 Prise en main & Démarrage
1. Lancez l'application sur votre smartphone ou émulateur.
2. L'application charge automatiquement les données géographiques. Une carte centrée sur Paris s'affiche instantanément, parsemée de repères géographiques.

## 🎨 Code Couleur des Marqueurs (Disponibilité)
Pour vous faire gagner du temps, l'état opérationnel des stations est directement visible via 3 couleurs de repères :
* 🟢 **Vert** : Station idéale (5 vélos ou plus disponibles immédiatement).
* 🟠 **Orange** : Station critique (Entre 1 et 4 vélos restants).
* 🔴 **Rouge** : Station vide ou indisponible (Aucun vélo disponible).

## 🔍 Barre de Recherche Intelligente
* Utilisez la barre de saisie textuelle située tout en haut : **"Rechercher une station..."**.
* Saisissez les premières lettres du nom d'une rue ou d'une station (ex: *Mairie*).
* La carte se filtre instantanément pour n'afficher que les stations correspondantes.
* *Astuce* : Si votre recherche n'aboutit qu'à un seul résultat unique, la carte effectue automatiquement un zoom immersif sur la station concernée.

## 📊 Visualisation des Détails
* Cliquez sur n'importe quel marqueur coloré pour afficher le nom de la station au sein d'une bulle d'information.
* Cliquez sur cette bulle pour ouvrir un volet inférieur rétractable (**BottomSheet**) affichant les compteurs précis :
    * Nombre exact de vélos mécaniques/électriques disponibles.
    * Nombre de points d'attache (bornes de quai) libres pour déposer un vélo.

## ⭐ Gestion des Favoris
* Depuis le volet de détails d'une station, cliquez sur le bouton **"⭐ Ajouter aux favoris"**.
* Pour consulter la liste de vos stations favorites, cliquez sur le bouton vert **"Favoris"** situé en bas à droite de l'écran principal.
* Vous pouvez à tout moment retirer une station de votre liste en cliquant sur **"❌ Retirer des favoris"**.
* *Note* : La base de données locale (Room) conserve vos favoris en mémoire. Vous pouvez y accéder même sans connexion Internet !

## 📍 Recherche de Proximité
* Cliquez sur le bouton bleu **"📍 Stations proches"** pour ouvrir le menu de ciblage par rayon géographique.
* Déplacez le curseur horizontal (**SeekBar**) pour étendre ou restreindre votre périmètre de recherche (de 100 mètres jusqu'à 2 kilomètres).
* La liste se met à jour dynamiquement et classe les stations de la plus proche à la plus lointaine, en affichant la distance exacte à parcourir.

## 🔄 Actualisation des données
L'application intègre un système de rafraîchissement asynchrone automatique. Toutes les **60 secondes**, l'application interroge les serveurs Open Data sans interrompre votre navigation pour mettre à jour l'état des stocks de vélos.