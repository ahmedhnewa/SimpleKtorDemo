package com.ahmedhnewa.utils.exceptions

open class NoPrivilegesException(role: String) : Exception(
    "The current user don't have access to this route: role = $role"
)

class NotAdminException : NoPrivilegesException("Admin")

class UserShouldAuthenticated(errorMessage: String) :
    Exception("User should be authenticated to do this action\n$errorMessage")

class UserShouldUnAuthenticated(errorMessage: String) :
    Exception("User should not be authenticated to do this action\n$errorMessage")