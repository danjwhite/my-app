$(document).ready(function () {

    // ************************ ADD NOTE MODAL ************************
    $('.newBtn').on('click', function () {
        $('.addNoteForm #title').val('');
        $('.addNoteForm #body').val('');
        $('.addNoteForm #addNoteModal').modal('show');
    });

    // ************************ EDIT NOTE MODAL ************************
    $('.editBtn').on('click', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

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
        const href = $(this).attr('href');

        $('#deleteNoteModal').find('#continueBtn').attr('href', href);
        $('#deleteNoteModal').modal('show');
    });

    // ************************ ADD NOTE BUTTON ************************
    $('#addNoteSubmitBtn').click(function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            title: $('#title').val(),
            body: $('#body').val()
        };

        $.ajax({
            type: 'POST',
            url: '/notes',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function () {
                $('#addNoteModal').modal('hide');
                location.reload();
            },
            error: function (xhr) {
                validateInput('.addNoteForm', xhr);
            }
        });
    });

    // ************************ EDIT NOTE BUTTON ************************
    $('#editNoteSubmitBtn').click(function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
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
            success: function () {
                $('#editNoteModal').modal('hide');
                location.reload();
            },
            error: function (xhr) {
                validateInput('.editNoteForm', xhr);
            }
        });
    });

    // ************************ CONFIRM DELETE NOTE BUTTON ************************
    $('#deleteNoteModal').find('#continueBtn').click(function (event) {
        event.preventDefault();

        $.ajax({
            type: 'DELETE',
            url: $('#deleteNoteModal').find('#continueBtn').attr('href'),
            success: function () {
                $('#deleteNoteModal').modal('hide');
                location.reload();
            }
        });
    });

    // ************************ SAVE NOTE CANCEL BUTTON ************************
    $('#addNoteModal').find('.exitBtn').click(function () {
        $('#addNoteModal').modal('hide');
        resetValidation();
    });

    // ************************ UPDATE NOTE CANCEL BUTTON ************************
    $('#editNoteModal').find('.exitBtn').click(function () {
        $('#editNoteModal').modal('hide');
        resetValidation();
    });

    // ************************ DELETE NOTE CANCEL BUTTON ************************
    $('#deleteNoteModal').find('.exitBtn').on('click', function () {
       $('#deleteNoteModal').modal('hide');
    });
});