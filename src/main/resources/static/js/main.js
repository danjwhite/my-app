// ************************ HELPER FUNCTIONS ************************
function validateInput(selector, xhr) {
    const response = JSON.parse(xhr.responseText);
    $.each(response.errors, function (key, value) {
        $(selector + ' .form-control[name=' + key + ']').addClass('is-invalid')
            .after('<span class="invalid-feedback">' + value + '</span>');
    });
}

// TODO: Refactor to accept a selector parameter
function resetValidation() {
    $('.form-control').each(function () {
        $(this).removeClass('is-invalid').next('span').remove();
    });
}