# ============================================================
# python_tutor_code.py
# Part D1: Code for Python Tutor — Step-by-step execution tracing
# 
# Adapted from LoginFrame.login() + UserService.authenticate()
# Load this into https://pythontutor.com/visualize.html
#
# Authors: Ramis Ali (22F-3703), Kamil Mohsin (22F-3713)
# ============================================================

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
