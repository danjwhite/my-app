$(document).ready(function () {

    /* ------------------------------ ADD USER BUTTON ------------------------------ */
    $('#addUserBtn').on('click', function () {
        $('.addUserForm #firstName').val('');
        $('.addUserForm #lastName').val('');
        $('.addUserForm #username').val('');
        $('.addUserForm #password').val('');
        $('.addUserForm #confirmPassword').val('');
        $('.addUserForm #addUserModal').modal('show');
    });

    /* ------------------------------ ADD USER SUBMIT BUTTON ------------------------------ */
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
            url: '/user-management/users',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#addUserModal').modal('hide');
                location.reload();
            },
            error: function(xhr){
                validateInput('.addUserForm', xhr);
            }
        });
    });

    /* ------------------------------ ADD USER CANCEL BUTTONS ------------------------------ */
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

    /* ------------------------------ EDIT USER BUTTON ------------------------------ */
    $('#userTable').on('click', '.editUserBtn', function () {
        const href = $(this).attr('href');

        $.get(href, function (user) {
            $('.editUserForm #username').val(user.username);
            $('.editUserForm #firstName').val(user.firstName);
            $('.editUserForm #lastName').val(user.lastName);
            $('.editUserForm #roleTypes').val(user.roleTypes);
        });

        $('#editUserSubmitBtn').attr('href', href);

        $('#editUserModal').modal('show');
    });

    /* ------------------------------ EDIT USER SUBMIT BUTTON ------------------------------ */
    $('#editUserSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const href = $(this).attr('href');

        const formData = {
            username: $('.editUserForm #username').val(),
            firstName: $('.editUserForm #firstName').val(),
            lastName: $('.editUserForm #lastName').val(),
            roleTypes: $('.editUserForm #roleTypes').val()
        };

        $.ajax({
            type: 'PUT',
            url: href,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#editUserModal').modal('hide');
                location.reload();
            },
            error: function (xhr) {
                validateInput('.editUserForm', xhr);
            }
        });
    });

    /* ------------------------------ EDIT USER CANCEL BUTTONS ------------------------------ */
    $('#editUserModal').find('.exitBtn').on('click', function () {
        $('#editUserModal').modal('hide');
        resetValidation()
    });

    /* ------------------------------ DELETE USER BUTTON ------------------------------ */
    $('#userTable').on('click', '.deleteUserBtn', function () {
        const href = $(this).attr('href');

        $('#deleteUserModal').find('#continueBtn').attr('href', href);
        $('#deleteUserModal').modal('show');
    });

    /* ------------------------------ DELETE USER CONFIRM BUTTON ------------------------------ */
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

    /* ------------------------------ DELETE USER CANCEL BUTTONS ------------------------------ */
    $('#deleteUserModal').find('.exitBtn').on('click', function () {
        $('#deleteUserModal').modal('hide');
    });

    /* ------------------------------ USER TABLE ------------------------------ */
    $('#userTable').DataTable({
        serverSide: true,
        lengthMenu: [10, 25, 50],
        ordering: true,
        paging: true,
        pagingType: "full_numbers",
        processing: true,
        autoWidth: true,
        ajax: {
            url: '/user-management/users',
            dataSrc: 'content',
            dataFilter: function (data) {
                const json = jQuery.parseJSON(data);

                // Add these for "Next" button functionality.
                json.recordsTotal = json.totalElements;
                json.recordsFiltered = json.totalElements;
                return JSON.stringify(json);
            },
            data: function (data) {
                // Add parameters for Spring.
                data.page = (data.start / data.length);
                data.size = data.length;
                data.sort = getSort(data);
                data.search = data.search.value;

                // Remove unnecessary parameters (optional).
                delete data.start;
                delete data.length;
                delete data.columns;
                delete data.order;
                delete data.draw;
            }
        },

        columnDefs: [
            {
                targets: 0,
                data: "username"
            },
            {
                targets: 1,
                data: "firstName"
            },
            {
                targets: 2,
                data: "lastName"
            },

            {
                targets: 3,
                data: function (data) {
                    return getActionToolbar(data);
                },
                orderable: false
            }
        ]
    });
});

function getActionToolbar(data) {
    const url = '/user-management/users/' + data.guid;

    return "<div class='btn-toolbar'>\n" +
        "   <button class='btn btn-sm btn-dark editUserBtn'\n" +
        "      href='" + url + "'><i\n" +
        "      class='fa fa-edit'></i></button>\n" +
        "   <button class='btn btn-sm btn-dark ml-3 deleteUserBtn'\n" +
        "      href='" + url + "'><i\n" +
        "      class='fa fa-trash-alt'></i></button>\n" +
        "</div>";
}

function getSort(data) {
    const index = data.order[0].column;
    const columnName = data.columns[index].data;
    const dir = data.order[0].dir;

    return columnName + ',' + dir;
}