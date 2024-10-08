**Phase 1:**

Introduction
This project implements the Phase 1 of an online system designed to manage users with multiple roles (Admin, Student, Instructor). The system ensures secure account creation, login, role-based access, and basic user management functionality.

**Key Features in Phase 1:**
1. Initial Admin Setup: The first user to log into the system is automatically assigned the Admin role.
2. Account Setup:
   - A user must specify a username and password (with password confirmation) to create an account.
   - After logging in, users are required to complete their account setup by providing an email address and their name (first, middle, last, and optionally preferred first name).
3. Role Management:
   - The system supports the roles of Admin, Student, and Instructor.
   - Users with multiple roles will be prompted to select the appropriate role for their session after logging in. Users with a single role are automatically directed to the homepage of their assigned role.
4. Admin Capabilities:
   - Invite Users: Admins can invite others to join the system by generating one-time invitation codes and assigning roles to the invited users.
   - Reset User Accounts: Admins can reset user accounts by setting a one-time password with an expiration date, requiring the user to create a new password on their next login.
   - Delete User Accounts: Admins can delete user accounts after confirming the action.
   - List User Accounts: Admins can view all user accounts along with their roles and names.
   - Manage User Roles: Admins can add or remove roles for any user.
5. General User Capabilities:
   - New users can establish an account using a one-time invitation code.
   - Users can log in and log out of the system. After logging in, they must complete their account setup and, if applicable, select a role for the session.

**Running the Project**
1. Initial Setup:
   - The first user who logs into the system will be prompted to create an Admin account by entering a username and password.
2. Inviting New Users (Admin Only):
   - Admins can generate one-time invitation codes to invite new users and assign roles to them.
3. Account Setup:
   - All users must complete their account setup (email and name) before gaining full access to the system.
4. Role Selection:
   - Users with multiple roles will need to select their session role after logging in.

**Role Homepages:**
- For Phase 1, the homepages for Student and Instructor roles contain only a "Log Out" option.

**Future Enhancements:**
- More features will be added to role-specific homepages in later phases of the project.

**Admin Functions:**
1. Invite Users: Generate one-time invitation codes for new users and assign roles.
2. Reset Account: Set one-time passwords for users.
3. Delete User: Remove a user from the system.
4. List Users: View all registered users and their roles.
5. Role Management: Add or remove roles from users.

**Dependencies**
- This phase does not require any additional dependencies beyond the base setup of the project environment.


This completes Phase 1 of the project. Future phases will introduce more functionality based on role-specific needs and extend the capabilities of the system.


