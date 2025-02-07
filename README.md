# 2025_compilation

## Pour commencer

Vous devez créer un “fork” du dépôt, avec le nom suivant : **2025_compilation_X_Y**, où X est le nom du premier membre du binôme et Y le nom du second membre du binôme, par exemple
_2025_compilation_macron_borne_.

Votre fork doit être PRIVE et vous devez ajouter comme membre en qualité de Maintainer votre responsable de TP.

Ensuite vous pourrez faire un clone du “nouveau” dépôt créé, où **monId** est votre identifiant etulab :

```console
# Vous demandera votre login/mdp à chaque pull/push
git clone https://etulab.univ-amu.fr/monId/2025_compilation_X_Y.git
# Ou alors, si votre clé SSH est bien configurée sur etulab
git clone git@etulab.univ-amu.fr:monID/2025_compilation_X_Y.git
```

Pour donner le nom **squelette**, par exemple, au dépôt d’origine afin de garder le lien avec lui, on fait :

```console
git remote add squelette https://etulab.univ-amu.fr/nasr/2025_compilation
```

Ensuite, lors des TP suivants, vous pourrez mettre à jour votre dépôt en faisant :

```console
git pull squelette main
```

## Utilisation

### Chaque TP

Le fichier **Compiler.java** est le fichier principal qui pilote les différentes parties du compilateur que nous allons construire.

À chaque TP, nous décommenterons une ligne du fichier **Compiler.java** afin d'ajouter la fonctionnalité que nous avons développée.

Exemple TP1 :

```java
	public static void main(String[] args) {
		processCommandLine(args);
		System.out.println("[BUILD SC] ");
//		buildSc(); // <-- décommenter cette ligne
//		System.out.println("[BUILD SA] ");
```

### TP1
Objectif : programmer un analyseur lexical et un analyseur syntaxique pour le langage L. Les deux analyseurs sont produits automatiquement avec le logiciel **sablecc**, à partir d’un fichier de spécification **l.cfg**.

* Générer les analyseurs pour la grammaire l.cfg
```console
2025-compilation/src$ java -jar ../sablecc/sablecc.jar l.cfg
```

* Compiler le compilateur
```console
2025-compilation/src$ javac Compiler.java
```

* Générer les analyseurs **et** compiler le compilateur
```console
2025-compilation/src$ make
```

* Compiler (analyser) un fichier de test (un programme en L) avec le compilateur
```console
2025-compilation/src$ java Compiler ../test/input/add1.l
```

* Compiler et générer l'arbre de derivation
```console
2025-compilation/src$ java Compiler -v 2 ../test/input/add1.l
```

* Examiner l'arbre généré
```console
2025-compilation/src$ less ../test/input/add1.sc
```

* Nettoyer (en cas d'erreurs)
```console
2025-compilation/src$ make clean
```

Il existe un programme qui permet de lancer tous les tests (lancer le compilateur sur tous les programmes de test en L) et évaluer leur resultat, appelé **evaluate.py** (répertoire **test**).