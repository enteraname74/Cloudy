## PENIN Noah
## DI LUNA Tommy

# INFO 910 Projet

> :warning: Le dépôt git utilisé pour ce projet est un dépôt personnel. Aussi, la manipulation de kubernetes et la réalisation du projet a été réalisé sur une seule machine 
> en cours pour des raisons pratiques. Cela explique le manque de commit de la part de mon collègue (travail à deux sur une même machine, une seule personne ayant fait des commits).
> 
> Si vous voulez voir uniquement les commits exacts du projet pour INFO910, regardez la branche `feature/pod-moi-le-minikube`

## Déployer l'application

> :warning: Les commandes qui vont suivre sont à éxecuter à la source du projet.

### Créer et utiliser une image du projet

Tout d'abord, il vous faut une image du projet. Vous pouvez utiliser celle disponible dans la partie release du dépôt github ou la construire avec la commande suivante :

```
./gradlew controller:buildImage
```

Il vous faudra ensuite charger l'image dans minikube.

> :warning: la système a été testé et validé avec le driver docker pour minikube.

Lier docker à minikube si besoin.

```
eval $(minikube docker-env)
```

```
// le chemin donné si dessous est celui lorsque l'image est générée avec la commande gradle donnée plus haut.
minikube image load controller/build/jib-image.tar
```

### Lancer minikube

```
minikube start --vm-driver=docker
```

Pensez à créer un tunnel pour que l'api soit accessible.

```
minikube tunnel
```

### Utiliser Kubernetes pour déployer l'application

```
 kubectl apply -f k8s
```

### Accéder à l'api
Vous trouverez l'url de l'api avec la commande suivante :
```
minikube service api --url
```

## Utiliser l'application

L'api permet de stocker et d'accéder aux données de fichiers audio.
Le projet est consituté des éléments suivants :
- api réalisé en Kotlin avec Ktor
- BDD Postgres

La plupart des requêtes du serveur nécessitent d'être authentifié (Bearer token). 
Il vous faudra vous créer un compte ou vous connecter pour récupérer un token à utiliser dans vos prochaines requêtes.

### Vérifier le bon fonctionnement du serveur
Une route permet de vérifier simplement si le serveur tourne :
```
curl {SERVER_URL}/hello

Si le serveur tourne, vous devriez avoir le résultat suivant :
Hello Ktor My Beloved!
```

### Création de compte
```
curl -X POST {SERVER_URL}/auth/sign -H 'Content-Type: application/json' -d '{"username":"GigaChad","password":"GigaChad"}'

Vous obtiendrez un résultat de la sorte :
{"token":"TOKEN_À_UTILISER"}
```

### Connexion
```
curl -X POST {SERVER_URL}/auth/login -H 'Content-Type: application/json' -d '{"username":"GigaChad","password":"GigaChad"}'

Vous obtiendrez un résultat de la sorte :
{"token":"TOKEN_À_UTILISER"}
```

### Envoyer une musique au serveur

```
curl -v {SERVER_URL}/music/upload -H "Authorization: Bearer TOKEN_À_UTILISER" -F "file=@{MUSIC_PATH};type=audio/mp4"
```

### Récupérer les informations des musiques envoyées
```
curl {SERVER_URL}/music/ofUser -H 'Content-Type: application/json' -H "Authorization: Bearer TOKEN_À_UTILISER"
```