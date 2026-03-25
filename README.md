# DDD Hexagonal avec Spring Boot — Tutoriel complet

> Projet d'exemple **TaskFlow** — API de gestion de tâches construite pour apprendre l'architecture DDD hexagonale avec Spring Boot, sans authentification, 100% orientée structure et bonnes pratiques.

---

## Qu'est-ce que le DDD ?

Le **Domain-Driven Design (DDD)** est une approche de conception logicielle qui place la **logique métier au centre** de l'application. L'idée fondatrice est simple : le code doit refléter le langage et les règles du domaine fonctionnel, pas les contraintes techniques.

En pratique, ça signifie que les règles de gestion — "une tâche ne peut pas être complétée deux fois", "le titre est obligatoire" — vivent dans des **entités Java pures**, sans annotation Spring ni JPA. Ces entités parlent le langage du métier, pas celui de la base de données.

---

## Qu'est-ce que l'architecture hexagonale ?

L'**architecture hexagonale** (aussi appelée *Ports & Adapters*) est un pattern structurel qui isole le cœur de l'application de tout ce qui est technique. Elle définit trois couches concentriques :

```
┌─────────────────────────────────────────┐
│           INFRASTRUCTURE                │  ← Adapters sortants
│  ┌───────────────────────────────────┐  │     JPA, Redis, SMTP, APIs...
│  │         APPLICATION               │  │  ← Adapters entrants
│  │  ┌─────────────────────────────┐  │  │     Controllers, Schedulers
│  │  │         DOMAIN              │  │  │  ← Logique métier pure
│  │  │  UseCase · Port · Event     │  │  │     POJO Java, 0 framework
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Règle absolue : les dépendances ne vont que vers l'intérieur.**  
Le Domain ne dépend de rien d'autre que de lui-même. Si une classe dans `domain/` importe `org.springframework.*` ou `javax.persistence.*`, c'est une violation.

---

## Pourquoi DDD et hexagonal ensemble ?

Le DDD définit **quoi** organiser : les entités, les règles métier, les événements, les cas d'utilisation.  
L'architecture hexagonale définit **comment** les isoler : en imposant une frontière technique entre le Domain et tout le reste.

Ensemble, ils résolvent le problème le plus commun des backends Spring Boot : le **God Service** — un `@Service` qui grossit sans fin, mélange logique métier et accès base de données, et devient impossible à tester sans démarrer toute l'application.

| Problème classique | Solution DDD hexagonal |
|---|---|
| `@Service` de 500 lignes | Un UseCase = une opération métier |
| Logique métier dans le Controller | Logique dans l'entité Domain |
| Impossible à tester sans DB | Injection d'un FakeRepository en mémoire |
| Changer de DB = modifier le métier | Nouvel Adapter, Domain inchangé |
| Couplage fort entre modules | Communication uniquement via Domain Events |

---

## Structure du projet

```
src/main/java/io/taskflow/
├── task/                               ← domaine "task"
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Task.java               ← POJO pur, logique métier
│   │   │   └── TaskStatus.java         ← enum PENDING / COMPLETED
│   │   ├── usecase/
│   │   │   ├── CreateTaskUseCase.java
│   │   │   ├── CompleteTaskUseCase.java
│   │   │   ├── GetTasksUseCase.java
│   │   │   ├── CreateTaskCommand.java  ← données d'entrée (record)
│   │   │   └── CreateTaskResult.java  ← données de sortie (record)
│   │   ├── port/
│   │   │   └── TaskRepositoryPort.java ← interface, contrat sortant
│   │   ├── event/
│   │   │   └── TaskCompletedEvent.java ← fait métier passé
│   │   └── exception/
│   │       └── TaskNotFoundException.java
│   ├── application/
│   │   ├── controller/
│   │   │   └── TaskController.java     ← adapteur HTTP entrant
│   │   ├── dto/
│   │   │   ├── CreateTaskRequest.java  ← payload JSON entrant
│   │   │   └── TaskResponse.java       ← payload JSON sortant
│   │   └── handler/
│   │       └── TaskExceptionHandler.java
│   └── infrastructure/
│       ├── persistence/
│       │   ├── TaskJpaEntity.java      ← @Entity Hibernate
│       │   ├── TaskJpaRepository.java  ← interface Spring Data
│       │   └── TaskRepositoryAdapter.java ← implémente TaskRepositoryPort
│       └── config/
│           └── TaskConfig.java         ← @Bean, câblage Spring
└── notification/                       ← domaine "notification"
    └── ...                             ← même structure

src/test/java/io/taskflow/
└── task/
    ├── FakeTaskRepository.java         ← double de test (Map en mémoire)
    ├── CreateTaskUseCaseTest.java
    └── CompleteTaskUseCaseTest.java
```

---

## Rôle de chaque package

### `domain/entity/`
Les entités métier. Ce sont des **POJO Java purs** : aucune annotation Spring ni JPA. Elles contiennent les règles de gestion via des méthodes métier (`complete()`) et exposent deux factory methods distinctes :
- `Task.create()` — crée une nouvelle tâche depuis une action utilisateur. Génère l'UUID, valide les champs, fixe le statut initial à `PENDING`.
- `Task.reconstruct()` — rehydrate une tâche chargée depuis la base de données, sans ré-exécuter les validations de création.

Le constructeur est `private` et reçoit tous les champs. Les champs immuables sont `final` (`id`, `title`, `assignedTo`, `createdAt`). Les champs modifiés par les méthodes métier sont non-`final` (`status`, `completedAt`).

### `domain/usecase/`
Les cas d'utilisation. **Une classe = une opération métier**. Chaque UseCase a un constructeur qui injecte ses dépendances (Ports) et une seule méthode `execute()`. Aucune annotation Spring — ils sont instanciés via des `@Bean` dans la Config.

### `domain/port/`
Les **interfaces** que le Domain définit pour exprimer ce dont il a besoin. Ces interfaces parlent uniquement en termes Domain : `Task`, `UUID`, `Optional<Task>`. Jamais `TaskJpaEntity`. C'est le contrat que l'Infrastructure implémente.

### `domain/event/`
Les **Domain Events** : des `record` Java immuables représentant un fait métier passé. Le UseCase les publie via `ApplicationEventPublisher` après la persistance. Les autres domaines s'y abonnent avec `@EventListener` sans couplage direct.

### `domain/exception/`
Les exceptions métier lancées par les UseCases et les entités. L'ExceptionHandler en Application les intercepte et les traduit en réponses HTTP.

### `application/controller/`
Les **adapteurs entrants**. Ils reçoivent les requêtes HTTP, convertissent les DTOs en Commands, appellent les UseCases, et convertissent les Results en réponses JSON. Aucune logique métier ici.

### `application/dto/`
Les objets de transport HTTP. Ils portent `@Valid`, `@NotBlank`, `@JsonProperty`. Ils ne rentrent jamais dans le Domain.

### `application/handler/`
Le `@RestControllerAdvice` qui intercepte les exceptions Domain et les traduit en réponses JSON avec le bon code HTTP.

### `infrastructure/persistence/`
Tout ce qui touche JPA vit ici et uniquement ici :
- `TaskJpaEntity` — la classe `@Entity` Hibernate avec `@Getter`, `@Setter`, `@NoArgsConstructor(PROTECTED)`. Elle expose `toDomain()` (JpaEntity → Task via `Task.reconstruct()`) et `from(Task)` (Task → JpaEntity via les setters).
- `TaskJpaRepository` — l'interface Spring Data utilisée uniquement par l'Adapter.
- `TaskRepositoryAdapter` — le `@Component` qui `implements TaskRepositoryPort`. C'est le seul endroit où `TaskJpaEntity` est manipulée. Une `TaskJpaEntity` ne sort jamais de cette classe.

### `infrastructure/config/`
La classe `@Configuration` qui déclare les UseCases en `@Bean` et leur injecte les Adapters via les Ports. C'est ici que Spring wire tout — le Domain ne sait pas que Spring existe.

---

## Comment les domaines communiquent

Dans un monolithe modulaire hexagonal, les domaines **ne s'injectent jamais directement**. La communication passe exclusivement par les **Domain Events**.

```
domaine task                       Spring                 domaine notification
     │                                │                           │
     │  events.publishEvent(          │                           │
     │    new TaskCompletedEvent()    │                           │
     │  )                             │                           │
     └───────────────────────────────►│                           │
                                      │  @EventListener           │
                                      │  onTaskCompleted()        │
                                      └──────────────────────────►│
                                                                   │
                                                          notifyUser.execute()
```

Le domaine `task` publie un fait sans savoir qui l'écoute.  
Le domaine `notification` réagit à ce fait sans savoir qui l'a publié.  
Les deux peuvent évoluer, être testés et être remplacés indépendamment.

Pour les rares cas où une réponse synchrone est nécessaire entre deux domaines (vérifier un quota avant de créer un post), on utilise un **Port synchrone** : une interface dans le Domain appelant, implémentée par un Adapter qui délègue au domaine cible. C'est un usage exceptionnel, explicitement documenté.

---

## Flux d'une requête de bout en bout

```
POST /api/tasks
      ↓
TaskController           [Application]    valide le DTO, crée CreateTaskCommand
      ↓
CreateTaskUseCase        [Domain]         appelle Task.create(), orchestre
      ↓
TaskRepositoryPort       [Domain]         interface — save(task)
      ↓
TaskRepositoryAdapter    [Infrastructure] TaskJpaEntity.from(task) → jpa.save()
      ↓
PostgreSQL
      ↓
ApplicationEventPublisher                 publie TaskCompletedEvent
      ↓
TaskEventListener        [notification]   @EventListener → NotifyUserUseCase
      ↓
201 Created  ◄────────────────────────────────────────────────── retour inverse
```

---

## Lancer le projet

```bash
# Cloner le projet
git clone https://github.com/...

# Démarrer l'application
./mvnw spring-boot:run
```

```bash
# Créer une tâche
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Apprendre le DDD", "description": "", "assignedTo": "alice"}'

# Compléter une tâche
curl -X PATCH http://localhost:8080/api/tasks/{id}/complete

# Lister les tâches d'un utilisateur
curl http://localhost:8080/api/tasks?assignedTo=alice
```

---

## Lancer les tests

```bash
./mvnw test
```

Les tests unitaires du Domain s'exécutent **sans Spring, sans base de données**. On instancie les UseCases à la main avec un `FakeTaskRepository` — une `Map` en mémoire qui `implements TaskRepositoryPort`. Tests en quelques millisecondes, sans infrastructure.

---

## Auteur

Tutoriel conçu et rédigé par **Victor Modjo**.

- 🔗 LinkedIn : [linkedin.com/in/victor-modjo](https://linkedin.com/in/victor-modjo)
- 🌐 Portfolio : [modjovictor.vercel.app](https://modjovictor.vercel.app)
