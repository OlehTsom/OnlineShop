package com.example.onlineshop.util.validatinon

import android.util.Log
import android.util.Patterns
import com.example.onlineshop.R
import com.example.onlineshop.ShopApp.Companion.getAppContext

fun validateEmail(email : String) : RegisterValidation {
    if(email.isEmpty())
        return RegisterValidation.Failed(getAppContext().getString(R.string.email_cannot_be_empty))
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed(getAppContext().getString(R.string.wrong_email_format))

    return RegisterValidation.Success
}

fun validatePassword(password: String) : RegisterValidation {
    if(password.isEmpty())
        return RegisterValidation.Failed(getAppContext().getString(R.string.password_cannot_be_empty))
    if(password.length < 6)
        return RegisterValidation.Failed(getAppContext().getString(R.string.password_should_contains_6_char))

    return RegisterValidation.Success
}

fun validateAddressTitle(address : String) : AddressValidation {
    if(address.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.address_title_cannot_be_empty))

    return AddressValidation.Success
}

fun validateFullName(fullName : String) : AddressValidation {
    if(fullName.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.full_name_cannot_be_empty))

    return AddressValidation.Success
}


fun validateStreetName(street : String) : AddressValidation {
    if(street.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.street_cannot_be_empty))

    return AddressValidation.Success
}

fun validatePhoneNumber(number : String) : AddressValidation {
    if(number.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.phone_number_cannot_be_empty))
    if (!Patterns.PHONE.matcher(number).matches())
        return AddressValidation.Failed(getAppContext().getString(R.string.wrong_phone_number_format))

    return AddressValidation.Success
}

fun validateCity(city : String) : AddressValidation {
    if(city.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.city_cannot_be_empty))

    return AddressValidation.Success
}

fun validateState(state : String) : AddressValidation {
    if(state.isEmpty())
        return AddressValidation.Failed(getAppContext().getString(R.string.state_cannot_be_empty))

    return AddressValidation.Success
}

fun validateMessInMessageSupport(mess : String) : MessageValidation {
    if(mess.isEmpty())
        return MessageValidation.Failed(getAppContext().getString(R.string.message_cannot_be_empty))

    if(mess.length <= 50)
        return MessageValidation.Failed(getAppContext().getString(R.string.the_message_must_contain_a_minimum_of_50_symbols))

    return MessageValidation.Success
}

fun validateNameInMessageSupport(name : String) : MessageValidation {
    if(name.isEmpty())
        return MessageValidation.Failed(getAppContext().getString(R.string.full_name_cannot_be_empty))

    return MessageValidation.Success
}

fun validateEmailInMessageSupport(email : String) : MessageValidation {
    if(email.isEmpty())
        return MessageValidation.Failed(getAppContext().getString(R.string.email_cannot_be_empty))
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return MessageValidation.Failed(getAppContext().getString(R.string.wrong_email_format))

    return MessageValidation.Success
}




