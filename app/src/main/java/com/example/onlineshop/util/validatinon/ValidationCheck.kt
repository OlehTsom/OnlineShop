package com.example.onlineshop.util.validatinon

import android.util.Patterns

fun validateEmail(email : String) : RegisterValidation {
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String) : RegisterValidation {
    if(password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")
    if(password.length < 6)
        return RegisterValidation.Failed("Password should contains 6 char")

    return RegisterValidation.Success
}

fun validateAddressTitle(address : String) : AddressValidation {
    if(address.isEmpty())
        return AddressValidation.Failed("Address title cannot be empty")

    return AddressValidation.Success
}

fun validateFullName(fullName : String) : AddressValidation {
    if(fullName.isEmpty())
        return AddressValidation.Failed("Full name cannot be empty")

    return AddressValidation.Success
}


fun validateStreetName(street : String) : AddressValidation {
    if(street.isEmpty())
        return AddressValidation.Failed("Street  cannot be empty")

    return AddressValidation.Success
}

fun validatePhoneNumber(number : String) : AddressValidation {
    if(number.isEmpty())
        return AddressValidation.Failed("Phone number cannot be empty")
    if (!Patterns.PHONE.matcher(number).matches())
        return AddressValidation.Failed("Wrong phone number format")

    return AddressValidation.Success
}

fun validateCity(city : String) : AddressValidation {
    if(city.isEmpty())
        return AddressValidation.Failed("City cannot be empty")

    return AddressValidation.Success
}

fun validateState(state : String) : AddressValidation {
    if(state.isEmpty())
        return AddressValidation.Failed("State cannot be empty")

    return AddressValidation.Success
}

fun validateMessInMessageSupport(mess : String) : MessageValidation {
    if(mess.isEmpty())
        return MessageValidation.Failed("State cannot be empty")
    if(mess.length <= 100)
        return MessageValidation.Failed("The message must contain a minimum of 100 symbols")

    return MessageValidation.Success
}

fun validateNameInMessageSupport(name : String) : MessageValidation {
    if(name.isEmpty())
        return MessageValidation.Failed("Name cannot be empty")

    return MessageValidation.Success
}

fun validateEmailInMessageSupport(email : String) : MessageValidation {
    if(email.isEmpty())
        return MessageValidation.Failed("Email cannot be empty")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return MessageValidation.Failed("Wrong email format")

    return MessageValidation.Success
}




