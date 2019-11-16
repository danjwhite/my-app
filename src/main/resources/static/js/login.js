$(document).ready(function () {

    // ************************ SET USER ROLE HIDDEN INPUT VALUE ************************
    populateRegistrationModal();

    // ************************ REGISTRATION MODAL ************************
    $('#registrationLink').on('click', function () {
        $('#registrationModal').modal('show');
    });

    // ************************ REGISTER BUTTON ************************
    $('#registrationSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            firstName: $('.registrationForm #firstName').val(),
            lastName: $('.registrationForm #lastName').val(),
            username: $('.registrationForm #username').val(),
            password: $('.registrationForm #password').val(),
            confirmPassword: $('.registrationForm #confirmPassword').val(),
            roleTypes: JSON.parse($('.registrationForm #roleTypes').val())
        };

        console.log(JSON.stringify(formData));

        $.ajax({
            type: 'POST',
            url: '/registration',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#addUserModal').modal('hide');
                window.location = "/";
            },
            error: function (xhr) {
                validateInput('.registrationForm', xhr);
            }
        });
    });

    // ************************ REGISTER CANCEL BUTTON ************************
    $('#registrationModal').find('.exitBtn').on('click', function () {
        resetValidation();
        $('.registrationForm #firstName').val('');
        $('.registrationForm #lastName').val('');
        $('.registrationForm #username').val('');
        $('.registrationForm #password').val('');
        $('.registrationForm #confirmPassword').val('');
        $('#registrationModal').modal('hide');
    })
});

function populateRegistrationModal() {
    const roles = [$('#userRoleVar').text()];
    $('.registrationForm #roleTypes').val(JSON.stringify(roles));
}