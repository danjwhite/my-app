$(document).ready(function () {

    const guid = $('#userGuid').text();
    const baseUrl = '/account-management/users/' + guid;

    // TODO: Determine approach for caching account info to prevent unnecessary calls to the database
    loadAccountInfo(baseUrl);

    // ************************ EDIT ACCOUNT MODAL ************************
    $('#editAccountBtn').on('click', function () {
        loadAccountInfo(baseUrl);
        $('#editAccountModal').modal('show');
    });

    // ************************ EDIT ACCOUNT SUBMIT BUTTON ************************
    $('#editAccountSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            username: $('.editAccountForm #username').val(),
            firstName: $('.editAccountForm #firstName').val(),
            lastName: $('.editAccountForm #lastName').val()
        };

        $.ajax({
            type: 'PUT',
            url: baseUrl,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#editAccountModal').modal('hide');
                loadAccountInfo(baseUrl);
            },
            error: function (xhr) {
                validateInput('.editAccountForm', xhr);
            }
        });
    });

    // ************************ EDIT ACCOUNT CANCEL BUTTON ************************
    $('#editAccountModal').find('.exitBtn').on('click', function () {
        $('#editAccountModal').modal('hide');
        resetValidation();
    });

    // ************************ CHANGE PASSWORD MODAL ************************
    $('#changePasswordLink').on('click', function () {
        $('#passwordModal').modal('show');
    });

    // ************************ CHANGE PASSWORD BUTTON ************************
    $('#passwordSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            username: $('.passwordForm #username').val(),
            password: $('.passwordForm #password').val(),
            newPassword: $('.passwordForm #newPassword').val(),
            confirmPassword: $('.passwordForm #confirmPassword').val()
        };

        $.ajax({
            type: 'PUT',
            url: baseUrl + '/password',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#passwordModal').modal('hide');
                clearPasswordForm();
            },
            error: function (xhr) {
                validateInput('.passwordForm', xhr);
            }
        });
    });

    // ************************ CANCEL PASSWORD CHANGE BUTTON ************************
    $('#passwordModal').find('.exitBtn').on('click', function () {
        $('#passwordModal').modal('hide');
        resetValidation();
        clearPasswordForm();
    });

    // ************************ DELETE ACCOUNT MODAL ************************
    $('#deleteAccountLink').on('click', function () {
        $('#deleteAccountModal').modal('show');
    });

    // ************************ DELETE ACCOUNT BUTTON ************************
    $('#deleteAccountModal').find('#continueBtn').on('click', function () {
        $.ajax({
            type: 'DELETE',
            url: baseUrl,
            success: function () {
                window.location = "/logout";
            }
        });
    });

    // ************************ DELETE ACCOUNT CANCEL BUTTONS ************************
    $('#deleteAccountModal').find('.exitBtn').on('click', function () {
        $('#deleteAccountModal').modal('hide');
    })
});

function loadAccountInfo(url) {
    $.get(url, function (user) {
        populateAccountPanel(user);
        populateAccountModal(user);
        populatePasswordModal(user);
    });
}

function populateAccountPanel(user) {
    $('.account-table #username').html(user.username);
    $('.account-table #firstName').html(user.firstName);
    $('.account-table #lastName').html(user.lastName);
}

function populateAccountModal(user) {
    $('.editAccountForm #username').val(user.username);
    $('.editAccountForm #firstName').val(user.firstName);
    $('.editAccountForm #lastName').val(user.lastName);
    $('.editAccountForm #roleTypes').val(JSON.stringify(user.roleTypes));
}

function populatePasswordModal(user) {
    $('.passwordForm #username').val(user.username);
}

function clearPasswordForm() {
    $('.passwordForm #password').val('');
    $('.passwordForm #newPassword').val('');
    $('.passwordForm #confirmPassword').val('');
}