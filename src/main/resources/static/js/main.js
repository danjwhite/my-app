// ************************ ADD NOTE MODAL ************************
$(document).ready(function () {
    $('.newBtn').on('click', function (event) {
        $('.addNoteForm #title').val('');
        $('.addNoteForm #body').val('');
        $('.addNoteForm #addNoteModal').modal('show');
    });

    // ************************ EDIT NOTE MODAL ************************
    $('.editBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function (note) {
            $('.editNoteForm #id').val(note.id);
            $('.editNoteForm #username').val(note.username);
            $('.editNoteForm #title').val(note.title);
            $('.editNoteForm #body').val(note.body);
        });

        $('.editNoteForm #editNoteModal').modal('show');
    });

    // ************************ DELETE NOTE MODAL ************************
    $('.deleteBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $('#confirmDeleteNoteBtn').attr('href', href);
        $('#deleteNoteModal').modal('show');
    });

    // ************************ ADD NOTE BUTTON ************************
    $('#addNoteSubmitBtn').click(function (event) {
        event.preventDefault();
        resetValidation();

        var formData = {
            title: $('#title').val(),
            body: $('#body').val()
        };

        $.ajax({
            type: 'POST',
            url: '/notes',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function (response) {
                validateInput(response, '#addNoteModal');
            }
        });
    });

    // ************************ EDIT NOTE BUTTON ************************
    $('#editNoteSubmitBtn').click(function (event) {
        event.preventDefault();
        resetValidation();

        var formData = {
            id: $('.editNoteForm #id').val(),
            username: $('.editNoteForm #username').val(),
            title: $('.editNoteForm #title').val(),
            body: $('.editNoteForm #body').val()
        };

        $.ajax({
            type: 'PUT',
            url: '/notes',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function (response) {
                validateInput(response, '#editNoteModal');
            }
        });
    });

    // ************************ CONFIRM DELETE NOT BUTTON ************************
    $('#confirmDeleteNoteBtn').click(function (event) {
        event.preventDefault();

        $.ajax({
            type: 'DELETE',
            url: $('#confirmDeleteNoteBtn').attr('href'),
            success: function (response) {
                $('#deleteNoteModal').modal('hide');
                location.reload();
            }
        });
    });

    // ************************ SAVE NOTE CANCEL BUTTON ************************
    $('#addNoteCancelBtn').click(function () {
        resetValidation();
    });

    // ************************ UPDATE NOTE CANCEL BUTTON ************************
    $('#editNoteCancelBtn').click(function () {
        resetValidation();
    });

    // ************************ HELPER FUNCTIONS ************************
    function validateInput(response, modalSelector) {
        if (!response.hasErrors) {
            $(modalSelector).modal('hide');
            location.reload();
        } else {
            $.each(response.errors, function (key, value) {
                $('.form-control[name=' + key + ']').addClass('is-invalid')
                    .after('<span class="invalid-feedback">' + value + '</span>');
            })
        }
    }
    
    function resetValidation() {
        $('.form-control').each(function () {
            $(this).removeClass('is-invalid').next('span').remove();
        });
    }
});