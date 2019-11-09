$(document).ready(function () {

    // ************************ ADD USER MODAL ************************
    $('#addUserBtn').on('click', function () {
        $('.addUserForm #firstName').val('');
        $('.addUserForm #lastName').val('');
        $('.addUserForm #username').val('');
        $('.addUserForm #password').val('');
        $('.addUserForm #confirmPassword').val('');
        $('.addUserForm #addUserModal').modal('show');
    });

    // ************************ EDIT USER MODAL ************************
    $('.editUserBtn').on('click', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

        $.get(href, function (user) {
            $('.editUserForm #username').val(user.username);
            $('.editUserForm #firstName').val(user.firstName);
            $('.editUserForm #lastName').val(user.lastName);
            $('.editUserForm #roleTypes').val(user.roleTypes);
        });

        $('#editUserModal').modal('show');
    });

    // ************************ DELETE USER MODAL ************************
    $('.deleteUserBtn').on('click', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

        $('#deleteUserModal').find('#continueBtn').attr('href', href);
        $('#deleteUserModal').modal('show');
    });

    // ************************ ADD USER BUTTON ************************
    $('#addUserSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            firstName: $('.addUserForm #firstName').val(),
            lastName: $('.addUserForm #lastName').val(),
            username: $('.addUserForm #username').val(),
            password: $('.addUserForm #password').val(),
            confirmPassword: $('.addUserForm #confirmPassword').val(),
            roleTypes: $('.addUserForm #roleTypes').val()
        };

        $.ajax({
            type: 'POST',
            url: '/users',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function (response) {
                if (validateInput('.addUserForm', response)) {
                    $('#addUserModal').modal('hide');
                    location.reload();
                }
            }
        });
    });

    // ************************ EDIT USER SUBMIT BUTTON ************************
    $('#editUserSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            username: $('.editUserForm #username').val(),
            firstName: $('.editUserForm #firstName').val(),
            lastName: $('.editUserForm #lastName').val(),
            roleTypes: $('.editUserForm #roleTypes').val()
        };

        $.ajax({
            type: 'PUT',
            url: '/users',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function (response) {
                if (validateInput('.editUserForm', response)) {
                    $('#editUserModal').modal('hide');
                    location.reload();
                }
            }
        });
    });

    // ************************ CONFIRM DELETE USER BUTTON ************************
    $('#deleteUserModal').find('#continueBtn').click(function (event) {
        event.preventDefault();

        $.ajax({
            type: 'DELETE',
            url: $('#deleteUserModal').find('#continueBtn').attr('href'),
            success: function () {
                $('#deleteUserModal').modal('hide');
                location.reload();
            }
        });
    });

    // ************************ DELETE USER CANCEL BUTTONS ************************
    $('#deleteUserModal').find('.exitBtn').on('click', function () {
        $('#deleteUserModal').modal('hide');
    });

    // ************************ ADD USER CANCEL BUTTON ************************
    $('#addUserModal').find('.exitBtn').on('click', function () {
        resetValidation();
        $('.addUserForm #firstName').val('');
        $('.addUserForm #lastName').val('');
        $('.addUserForm #username').val('');
        $('.addUserForm #password').val('');
        $('.addUserForm #confirmPassword').val('');

        const defaultRole = $('#defaultRoleVar').text();
        $('.addUserForm #roleTypes').val([defaultRole]);

        $('#addUserModal').modal('hide');
    });

    // ************************ EDIT USER CANCEL BUTTON ************************
    $('#editUserModal').find('.exitBtn').on('click', function () {
        $('#editUserModal').modal('hide');
        resetValidation()
    });
});