# PART D — Dynamic Program Analysis [10 Marks]

## Selected Method

**Class:** `UserService.java`  
**Method:** `authenticate(String username, String password)` — traced through `UserDAO.authenticate()` and `LoginFrame.login()`  
**Justification:** Contains conditional statements (null checks, credential comparison), uses 4+ local variables, and represents the critical authentication pathway.

For Python Tutor, we use a self-contained Python adaptation of the `login()` method from `LoginFrame.java` (lines 194–217) combined with `UserService.authenticate()`:

```python
# Adapted from LoginFrame.login() (Line 194) + UserService.authenticate()
# Self-contained version for Python Tutor execution

# Simulated user database (from data/users.txt)
users_db = {
    "admin": {"password": "admin", "fullName": "Administrator", "role": "ADMIN", "active": True},
    "doctor1": {"password": "doc123", "fullName": "Dr. Ahmed", "role": "DOCTOR", "active": True},
    "nurse1": {"password": "nurse123", "fullName": "Sara Malik", "role": "NURSE", "active": False}
}

def authenticate(username, password):
    """UserService.authenticate() — Lines 26-28 of UserService.java"""
    if username is None or username.strip() == "":
        return None
    if password is None or password.strip() == "":
        return None
    
    # UserDAO.authenticate() logic
    user = users_db.get(username)
    if user is None:
        return None
    
    if user["password"] == password and user["active"]:
        return user
    return None

def login(username_input, password_input):
    """LoginFrame.login() — Lines 194-217 of LoginFrame.java"""
    username = username_input.strip()
    password = password_input.strip()
    
    if username == "" or password == "":
        result = "ERROR: Username and password cannot be empty"
        return result
    
    user = authenticate(username, password)
    
    if user is not None:
        result = "SUCCESS: Welcome, " + user["fullName"] + " (" + user["role"] + ")"
    else:
        result = "ERROR: Invalid username or password"
    
    return result

# Test case: Successful admin login
output = login("admin", "admin")
print(output)
```

---

## D1. Execution Trace with Python Tutor [4 Marks]

### Execution Trace Table

| Step | Statement Executed | Variables Before | Variables After | Notes |
|------|-------------------|------------------|-----------------|-------|
| 1 | `username = username_input.strip()` | username_input="admin" | username="admin" | Input trimmed |
| 2 | `password = password_input.strip()` | password_input="admin" | password="admin" | Input trimmed |
| 3 | `if username == "" or password == "":` | username="admin", password="admin" | (no change) | Condition evaluates to **False** — both non-empty |
| 4 | `user = authenticate(username, password)` | username="admin", password="admin" | (enters authenticate function) | Function call — new frame created |
| 5 | `if username is None or username.strip() == "":` | username="admin" | (no change) | Condition **False** — username is valid |
| 6 | `if password is None or password.strip() == "":` | password="admin" | (no change) | Condition **False** — password is valid |
| 7 | `user = users_db.get(username)` | username="admin" | user={"password":"admin", "fullName":"Administrator", "role":"ADMIN", "active":True} | Dictionary lookup succeeds |
| 8 | `if user is None:` | user={...} | (no change) | Condition **False** — user found |
| 9 | `if user["password"] == password and user["active"]:` | user["password"]="admin", password="admin", user["active"]=True | (no change) | Condition **True** — credentials match AND account active |
| 10 | `return user` | | Returns user dict to caller | authenticate() returns successfully |
| 11 | `if user is not None:` | user={"password":"admin",...} | (no change) | Condition **True** — authentication succeeded |
| 12 | `result = "SUCCESS: Welcome, " + user["fullName"] + ...` | | result="SUCCESS: Welcome, Administrator (ADMIN)" | Welcome message constructed |
| 13 | `return result` | | Returns success string | login() completes |
| 14 | `print(output)` | output="SUCCESS: Welcome, Administrator (ADMIN)" | | Output printed to console |

> **Insert:** Python Tutor screenshot at **Step 9** — the moment the conditional `if user["password"] == password and user["active"]` is evaluated. The frame panel should show: `username="admin"`, `password="admin"`, `user={"password": "admin", ...}`.

### Branch Analysis

At Step 9, the conditional `user["password"] == password and user["active"]` evaluates to **True**. The `True` branch was taken because both sub-conditions were satisfied: the stored password `"admin"` matched the input password `"admin"` (string equality), AND the `active` flag was `True`. If either condition had failed — for example, if the user were `"nurse1"` whose `active` flag is `False` — the branch would have fallen through to `return None`, and the caller (`login()`) would have displayed the "Invalid username or password" error. This demonstrates short-circuit evaluation: Python's `and` operator would not have checked `user["active"]` if the password comparison had already returned `False`.

---

## D2. Control Flow Graph [3 Marks]

### CFG Description (Draw in Draw.io)

```
[ENTRY] ──→ [B1: username = input.strip(); password = input.strip()]
                │
                ▼
          [D1: username=="" OR password==""] ──True──→ [B2: result = "ERROR: empty"] ──→ [EXIT]
                │ False
                ▼
          [B3: user = authenticate(username, password)]
                │
                ▼
          [D2: user is not None?] ──True──→ [B4: result = "SUCCESS: Welcome..."] ──→ [EXIT]
                │ False
                ▼
          [B5: result = "ERROR: Invalid..."] ──→ [EXIT]
```

**Inside `authenticate()`:**
```
[ENTRY_AUTH] ──→ [D3: username None or empty?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [D4: password None or empty?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [B6: user = users_db.get(username)]
                        │
                        ▼
                  [D5: user is None?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [D6: password matches AND active?] ──True──→ [return user] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [return None] ──→ [EXIT_AUTH]
```

> **Highlight the path taken in D1** (Steps 1→14) with a **blue/bold border** in Draw.io.

### CFG Analysis

**(1) Independent Paths:**
- Path 1: B1 → D1(True) → B2 → EXIT (empty input)
- Path 2: B1 → D1(False) → B3 → [D3(True)] → D2(False) → B5 → EXIT (null username in auth)
- Path 3: B1 → D1(False) → B3 → [D3(F)→D4(True)] → D2(False) → B5 → EXIT (null password)
- Path 4: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(True)] → D2(False) → B5 → EXIT (user not found)
- Path 5: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(F)→D6(False)] → D2(False) → B5 → EXIT (wrong password or inactive)
- Path 6: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(F)→D6(True)] → D2(True) → B4 → EXIT (**success path — taken in D1**)

**Total independent paths: 6**

**(2) Cyclomatic Complexity:**
- Nodes (N) = 12 (B1, D1, B2, B3, D2, B4, B5, D3, D4, D5, D6, B6)
- Edges (E) = 17
- CC = E − N + 2 = 17 − 12 + 2 = **7**
- SonarQube would report CC ≈ 6–7 for the combined login + authenticate flow, consistent with our calculation.

**(3) Reducing CC by 1:**
Replace the two null/empty checks in `authenticate()` (D3 and D4) with a single guard clause: `if not username or not password: return None`. This merges two decision nodes into one, reducing E by 1 and N by 1, resulting in CC = 6.

---

## D3. Abstract Syntax Tree Inspection [3 Marks]

### AST Explorer Configuration
- **URL:** https://astexplorer.net
- **Parser:** Java (select "java" language)
- **Code loaded:** The `authenticate()` method from UserService.java

> **Insert:** Screenshot of AST Explorer with the tree expanded to at least 3 levels.

### Node Type Annotations

| Node Type | Location in AST | Description |
|-----------|----------------|-------------|
| **MethodDeclaration** | Root level | Node named `authenticate` with return type `User`, two parameters (`username: String`, `password: String`) |
| **VariableDeclaration** | Inside method body → Block | `User user = userDAO.authenticate(username, password);` — declares local variable `user` of type `User` |
| **IfStatement** | First child of Block | `if (username == null \|\| username.strip().equals(""))` — guard clause checking empty input |
| **ReturnStatement** | Inside IfStatement.consequent | `return null;` — early return when validation fails |
| **BinaryExpression** | Inside IfStatement.test | `username == null \|\| username.strip().equals("")` — binary OR expression with two operands |

### IfStatement Internal Structure

The IfStatement node in the Java AST has three children: **test** (the boolean condition), **consequent** (the block executed when test is true), and **alternate** (the else block, which is null for a simple if-without-else). The `test` child is a `BinaryExpression` with operator `||`, whose left operand is another `BinaryExpression` (`username == null`) and whose right operand is a `MethodInvocation` (`username.strip().equals("")`). This tree structure reveals that Java represents branching as a recursive composition of expressions — the `||` operator itself is a node whose children are its operands, not a flat list of conditions.

### Practical Re-Engineering Use Case for ASTs

A **refactoring engine** (like IntelliJ IDEA's built-in refactoring tools or a custom Checkstyle rule) uses ASTs to perform safe, automated code transformations. For example, to implement the "Replace Data Value with Object" refactoring for our Primitive Obsession smell, a tool would: (1) parse all Java files into ASTs, (2) find all `VariableDeclaration` nodes where the type is `String` and the variable name matches a pattern like `phoneNumber` or `email`, (3) generate a new class `PhoneNumber` with a constructor that validates the format, and (4) rewrite the AST to replace `String phoneNumber = "0312-9876543"` with `PhoneNumber phoneNumber = new PhoneNumber("0312-9876543")`. Because the transformation operates on the structured AST rather than raw text, it correctly handles edge cases like string concatenation, method parameters, and return types that a regex-based find-and-replace would miss.
