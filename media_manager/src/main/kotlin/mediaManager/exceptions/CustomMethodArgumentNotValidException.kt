package mediaManager.exceptions

import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException

class CustomMethodArgumentNotValidException(
    parameter: MethodParameter,
    bindingResult: BindingResult,
    override val message: String,
) : MethodArgumentNotValidException(parameter, bindingResult)
