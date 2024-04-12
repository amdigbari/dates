package mediaManager.exceptions

class CustomIllegalArgumentException(message: String, var fieldName: String) : IllegalArgumentException(message)
