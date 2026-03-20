# 2025_compilation

## Pour commencer

Vous devez créer un “fork” du dépôt

## Utilisation


Le fichier **Compiler.java** est le fichier principal qui pilote les différentes parties du compilateur que nous allons construire.


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
