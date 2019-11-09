// ************************ HELPER FUNCTIONS ************************
function validateInput(selector, response) {
    if (response.hasErrors) {
        $.each(response.errors, function (key, value) {
            $(selector + ' .form-control[name=' + key + ']').addClass('is-invalid')
                .after('<span class="invalid-feedback">' + value + '</span>');
        });

        return false;
    } else {
        return true;
    }
}

// TODO: Refactor to accept a selector parameter
function resetValidation() {
    $('.form-control').each(function () {
        $(this).removeClass('is-invalid').next('span').remove();
    });
}