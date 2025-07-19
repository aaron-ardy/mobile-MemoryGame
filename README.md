# mobile-MemoryGame
a project for IT140P

## Development Tasks Overview

### UI Component Roadmap (as of now)

| Component           | TODO                                                                 |
|---------------------|----------------------------------------------------------------------|
| Pulse Animation      | Fetch pattern via `getTileSequence.php` and animate tiles accordingly |
| User Interaction     | Log user tap order locally (tapping of tile sequence)                |
| Submit Button Logic  | Compare input vs backend sequence and show result                   |
| Hint Button          | Limited replay functionality with counter decrement                 |
| Back Button          | Wire up `navController.popBackStack()` or navigate to `"menu"`      |

---

### 🌐 REST API Integration Plan (xampp, phpmyadmin)

| API Name          | Function                                                                 |
|-------------------|--------------------------------------------------------------------------|
| `getTileSequence` | Provides the pulse sequence the player needs to memorize                 |
| `getLeaderboard`  | Returns a ranked list of top scores for leaderboard screen               |
| `updateDifficulty`| Lets users change difficulty settings and saves it on DB (XAMPP backend) |
| idk | supposedly 4 daw need eh |
