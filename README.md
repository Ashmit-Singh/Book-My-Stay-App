<div align="center">

<br/>

```
██████╗  ██████╗  ██████╗ ██╗  ██╗    ███╗   ███╗██╗   ██╗
██╔══██╗██╔═══██╗██╔═══██╗██║ ██╔╝    ████╗ ████║╚██╗ ██╔╝
██████╔╝██║   ██║██║   ██║█████╔╝     ██╔████╔██║ ╚████╔╝ 
██╔══██╗██║   ██║██║   ██║██╔═██╗     ██║╚██╔╝██║  ╚██╔╝  
██████╔╝╚██████╔╝╚██████╔╝██║  ██╗    ██║ ╚═╝ ██║   ██║   
╚═════╝  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝    ╚═╝     ╚═╝   ╚═╝  

███████╗████████╗ █████╗ ██╗   ██╗
██╔════╝╚══██╔══╝██╔══██╗╚██╗ ██╔╝
███████╗   ██║   ███████║ ╚████╔╝ 
╚════██║   ██║   ██╔══██║  ╚██╔╝  
███████║   ██║   ██║  ██║   ██║   
╚══════╝   ╚═╝   ╚═╝  ╚═╝   ╚═╝  
```

### Hotel Booking Management System

*Core Java · Data Structures · Facade Pattern · Real-World Engineering*

<br/>

![Java](https://img.shields.io/badge/Java-21-F89820?style=flat-square&logo=openjdk&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing-22c55e?style=flat-square)
![Pattern](https://img.shields.io/badge/Pattern-Facade-8b5cf6?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-3b82f6?style=flat-square)
![DSA](https://img.shields.io/badge/DSA-Applied-f59e0b?style=flat-square)

<br/>

> **The problem with studying data structures in isolation:**  
> You learn what a `PriorityQueue` *is* — but never *why* you'd reach for one.  
> This project fixes that.

<br/>

</div>

---

## What Is This?

**Book My Stay** is a hotel booking management system built entirely in Core Java — no frameworks, no Spring, no magic. Every data structure in the codebase was chosen to solve a *specific, real operational problem*, not to demonstrate that you know it exists.

The architecture deliberately mirrors how production systems grow: each new use case introduces one new concept while compounding everything built before it. By the end, you have a working system *and* the engineering intuition to explain every decision in it.

**Who is this for?**
- Java learners who want to see DSA applied in a realistic context
- Engineers preparing for system design discussions
- Anyone tired of textbook examples that go nowhere

---

## Five Use Cases. Five Real Problems.

<br/>

### `UC1` · Room Inventory Management
**Structures:** `HashMap<String, Room>` + `ArrayList<Room>`

The front desk needs to answer *"Is room 201 available?"* in constant time — not by scanning a list of 400 rooms.

```
Guest asks → HashMap.get("201") → O(1). Done.
```

| Challenge | Design Decision |
|---|---|
| Fast lookup by room number | `HashMap` — keys are unique, retrieval is O(1) |
| Reject duplicate room numbers | `HashMap` enforces key uniqueness at insertion |
| Downstream filtering & sorting | Return `ArrayList` — mutable, ordered, stream-friendly |

```java
// Instant lookup — no loops, no scans, no matter the hotel size
Optional<Room> room = inventoryService.findRoom("201");
```

<br/>

---

### `UC2` · FIFO Booking Queue
**Structure:** `ArrayDeque<Booking>` used as a Queue

Guests must be processed in the order they arrived. Full stop. No exceptions, no priority, no skipping.

```
Alice arrives → Bob arrives → Charlie arrives
Process order: Alice → Bob → Charlie  [guaranteed by the data structure]
```

| Challenge | Design Decision |
|---|---|
| Enforce strict arrival order | Queue's FIFO contract makes it structurally impossible to skip |
| Prevent accidental priority injection | Only `addLast` / `pollFirst` are exposed — the API enforces fairness |
| Performance over `LinkedList` | `ArrayDeque` has better cache locality and zero per-node overhead |

```java
queue.enqueue(booking);              // Guest joins the back of the line
Booking next = queue.processNext();  // Serve whoever arrived first
```

> **Why `ArrayDeque` over `LinkedList`?**  
> Both implement `Deque`. `ArrayDeque` is backed by a resizable array — better cache performance, lower memory footprint, no pointer chasing. Prefer it whenever random access isn't needed.

<br/>

---

### `UC3` · Waitlist & Cancellation Undo
**Structures:** `PriorityQueue<Booking>` (min-heap) + `ArrayDeque<Booking>` as a Stack

Two problems live here. One structure isn't enough.

**Problem A — Weighted fairness:** VIP guests should move up the waitlist ahead of standard guests. "Most recent" logic won't work — we need priority-ordered promotion.

**Problem B — Undo cancellations:** When a cancellation is reversed, the most recently cancelled booking should be restored first.

```
Waitlist (PriorityQueue):          Cancellation Stack (ArrayDeque):
  priority=1 [VIP]    ← poll 1st    cancel(BK-04) → push
  priority=3 [Gold]                 cancel(BK-07) → push
  priority=5 [Regular]← poll last   undo          ← pop BK-07 first
```

| Challenge | Design Decision |
|---|---|
| VIP guests promoted ahead of others | `PriorityQueue` orders by `Booking.priority` automatically |
| "Undo" means most-recent-first | Stack (LIFO) gives O(1) access to the latest cancellation |
| Two different orderings needed | Two different structures — each purpose-fit |

```java
Booking nextUp   = waitlistService.promoteNext();           // Highest-priority guest
Booking restored = waitlistService.undoLastCancellation();  // Most recently cancelled
```

> **UC2 vs UC3 — the key contrast:**  
> UC2 is arrival-ordered (FIFO) — fairness by time.  
> UC3 is priority-ordered (heap) — fairness by tier.  
> Same word "queue." Completely different contracts.

<br/>

---

### `UC4` · Double-Booking Prevention
**Structures:** `HashSet<String>` (occupancy keys) + `HashMap<String, Booking>`

Two guests cannot occupy the same room on the same night. The check must be instant regardless of how many bookings exist.

The key insight: a booking conflict exists if and only if two stays share a `(room, date)` pair. Encode that pair as a composite string key.

```
New booking: Room 201, Mar 22–24
Keys inserted into HashSet:
  "201|2026-03-22"  "201|2026-03-23"  "201|2026-03-24"

Incoming booking: Room 201, Mar 23–25
Check "201|2026-03-23" → already present → REJECTED immediately
```

| Challenge | Design Decision |
|---|---|
| Detect overlapping date ranges fast | Composite key encodes both room and date — one lookup per night |
| O(1) availability check at any scale | `HashSet.contains()` is O(1) regardless of hotel occupancy |
| Release dates on cancellation | `HashSet.remove()` frees slots atomically |

```java
// One date = one key. If any key already exists, the room is taken.
boolean available = registryService.isAvailable("201", checkIn, checkOut);
```

> **Key design insight:**  
> Two bookings conflict if and only if they share a key in the set. The composite key collapses a two-dimensional overlap problem into a single membership check.

<br/>

---

### `UC5` · Room Search & Sort
**API:** `Comparable<Room>` + `Comparator<Room>`

Guests filter and sort rooms in multiple ways: cheapest first, by type, by availability. The sorting logic must be composable without modifying the `Room` class for every new requirement.

```
Natural order  (Comparable):   Room implements Comparable → sorted by price by default
Custom order   (Comparator):   BY_TYPE.thenComparing(BY_PRICE_ASC) → chain strategies
Single-pass min (Stream):      Stream.min(comparator) → cheapest available suite in O(n)
```

| Challenge | Design Decision |
|---|---|
| Default sort (price ascending) | `Room implements Comparable` — natural ordering baked in |
| Sort by type, then price | `Comparator` chains via `.thenComparing()` — no class modification |
| Find cheapest available room | `Stream.min(Comparator)` — single-pass, readable, correct |

```java
// Composable comparator catalogue — mix and match freely
Comparator<Room> BY_TYPE_THEN_PRICE = BY_TYPE.thenComparing(BY_PRICE_ASC);

// Find the cheapest available suite in one pass
Optional<Room> best = searchService.cheapestAvailable(rooms, RoomType.SUITE);
```

---

## Architecture

All five services are wired together through a single `HotelSystem` facade. Nothing outside it touches the individual services directly.

```
                    ┌─────────────────────────┐
                    │       HotelSystem        │
                    │         (Facade)         │
                    │   Single entry point     │
                    └────────────┬────────────┘
                                 │
           ┌─────────────────────┼──────────────────────┐
           │                     │                      │
           ▼                     ▼                      ▼
  ┌────────────────┐   ┌──────────────────┐   ┌─────────────────┐
  │ RoomInventory  │   │  BookingQueue    │   │    Waitlist     │
  │    Service     │   │    Service       │   │    Service      │
  │                │   │                  │   │                 │
  │ HashMap<K,Room>│   │ ArrayDeque<Book> │   │ PriorityQueue   │
  │ ArrayList<Room>│   │   (FIFO Queue)   │   │ + ArrayDeque    │
  └────────────────┘   └──────────────────┘   │   (Stack)       │
                                               └─────────────────┘
           ┌──────────────────────────────────────────┐
           │                                          │
           ▼                                          ▼
  ┌────────────────────┐                  ┌──────────────────┐
  │  BookingRegistry   │                  │  RoomSearch      │
  │     Service        │                  │    Service       │
  │                    │                  │                  │
  │ HashSet<String>    │                  │ Comparable<Room> │
  │ HashMap<K,Booking> │                  │ Comparator<Room> │
  └────────────────────┘                  └──────────────────┘
```

**Why Facade?**

Without it, a caller could write directly to the booking registry and skip the double-booking check entirely. The facade eliminates that class of bug structurally — you cannot bypass an invariant you cannot see. This mirrors how real production APIs are built: one stable public surface, implementation details hidden, correctness enforced at the boundary.

---

## Project Structure

```
src/hotel/
│
├── model/
│   ├── Room.java                    # Implements Comparable<Room> — natural price order
│   ├── Guest.java                   # Value object; equals/hashCode keyed on guestId
│   └── Booking.java                 # Implements Comparable<Booking> — ordered by priority
│
├── service/
│   ├── RoomInventoryService.java    # UC1 · HashMap + ArrayList
│   ├── BookingQueueService.java     # UC2 · ArrayDeque as FIFO Queue
│   ├── WaitlistService.java         # UC3 · PriorityQueue + ArrayDeque as Stack
│   ├── BookingRegistryService.java  # UC4 · HashSet + HashMap
│   ├── RoomSearchService.java       # UC5 · Comparable + Comparator
│   └── HotelSystem.java             # Facade — orchestrates all services
│
├── util/
│   └── IdGenerator.java             # AtomicInteger-based ID sequences
│
└── main/
    └── HotelDemo.java               # End-to-end demo runner — all 5 use cases
```

---

## Data Structure Quick Reference

| Use Case | Structure | Complexity | The Actual Reason |
|---|---|---|---|
| Room Inventory | `HashMap` | O(1) get/put | Lookup by room number without scanning |
| Booking Queue | `ArrayDeque` (Queue) | O(1) enqueue/dequeue | FIFO contract enforced at API level |
| Waitlist Priority | `PriorityQueue` | O(log n) insert/poll | VIP guests auto-promoted without manual sorting |
| Cancellation Undo | `ArrayDeque` (Stack) | O(1) push/pop | Most-recent cancellation restored first |
| Double-Booking Guard | `HashSet` | O(1) contains/add | Conflict detection regardless of hotel size |
| Search & Sort | `Comparable`/`Comparator` | O(n log n) sort | Composable strategies without modifying `Room` |

---

## Running It

**Requires:** Java 11+

```bash
# Clone
git clone https://github.com/Ashmit-Singh/Book-My-Stay-App.git
cd Book-My-Stay-App

# Compile
javac -d out $(find src -name "*.java")

# Run
java -cp out hotel.main.HotelDemo
```

**Expected output (excerpt):**

```
============================================================
  USE CASE 1: Room Inventory Management
============================================================
Room[101 | SINGLE | $89.00/night  | AVAILABLE]
Room[201 | DOUBLE | $149.00/night | AVAILABLE]
Room[301 | SUITE  | $299.00/night | AVAILABLE]

Duplicate room rejected: Room 101 already exists.

============================================================
  USE CASE 2: FIFO Booking Queue
============================================================
[Queue] Request enqueued: BK-1000 | Queue size: 1
[Queue] Processing: BK-1000 (Guest: Alice Chen)
[Registry] Confirmed: BK-1000 | 3 nights blocked
```

---

## What Could Come Next

Each extension isn't just a feature — it introduces a meaningfully new concept:

| Extension | New Concept |
|---|---|
| Persist bookings to JSON/CSV | Serialization, `ObjectMapper` |
| Thread-safe operations | `ConcurrentHashMap`, `ReentrantLock` |
| Interactive CLI menu | `Scanner`, event loop pattern |
| Billing & invoice generation | `TreeMap<LocalDate, LineItem>` — date-ordered traversal |
| Multi-property support | Composite keys, nested maps |

---

## Author

**Ashmit Singh** — [github.com/Ashmit-Singh](https://github.com/Ashmit-Singh)

---

<div align="center">

*Built to answer the question nobody asks in DSA class:*  
*"But when would I actually use this?"*

</div>
