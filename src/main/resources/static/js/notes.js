$(document).ready(function () {

    const userGuid = $('#userGuid').text();

    // ************************ NOTE TABLE ************************
    $('#noteTable').DataTable({
        "serverSide": true,
        "lengthMenu": [10, 25, 50],
        ordering: false,
        paging: true,
        pagingType: "full_numbers",
        processing: true,
        autoWidth: true,
        ajax: {
            url: '/users/' + userGuid + '/notes',
            dataSrc: 'content',
            dataFilter: function (data) {
                const json = jQuery.parseJSON(data);

                // Add these for "Next" button functionality.
                json.recordsTotal = json.totalElements;
                json.recordsFiltered = json.totalElements;
                return JSON.stringify(json);
            },
            "data": function (data) {
                // Add parameters for Spring
                data.page = (data.start / data.length);
                data.size = data.length;
                // data.sort = getSort(data);
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
                "targets": 0,
                "data": function (data) {
                    return getNote(data);
                }
            }
        ]
    });

    // ************************ ADD NOTE MODAL ************************
    $('#addNoteBtn').on('click', function () {
        $('.addNoteForm #title').val('');
        $('.addNoteForm #body').val('');
        $('.addNoteForm #addNoteModal').modal('show');
    });

    // ************************ EDIT NOTE MODAL ************************
    $('#noteTable').on('click', '.editNoteBtn', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

        $.get(href, function (note) {
            $('.editNoteForm #id').val(note.id);
            $('.editNoteForm #username').val(note.username);
            $('.editNoteForm #title').val(note.title);
            $('.editNoteForm #body').val(note.body);
        });

        $('#editNoteModal').find('#editNoteSubmitBtn').attr('href', href);
        $('#editNoteModal').modal('show');
    });

    // ************************ DELETE NOTE MODAL ************************
    $('#noteTable').on('click', '.deleteNoteBtn', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

        $('#deleteNoteModal').find('#continueBtn').attr('href', href);
        $('#deleteNoteModal').modal('show');
    });

    // ************************ ADD NOTE BUTTON ************************
    $('#addNoteSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const formData = {
            title: $('#title').val(),
            body: $('#body').val()
        };

        $.ajax({
            type: 'POST',
            url: '/users/' + userGuid + '/notes',
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
    $('#editNoteSubmitBtn').on('click', function (event) {
        event.preventDefault();
        resetValidation();

        const href = $(this).attr('href');
        const formData = {
            title: $('.editNoteForm #title').val(),
            body: $('.editNoteForm #body').val()
        };

        $.ajax({
            type: 'PUT',
            url: href,
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
    $('#deleteNoteModal').find('#continueBtn').on('click', function (event) {
        event.preventDefault();
        const href = $(this).attr('href');

        $.ajax({
            type: 'DELETE',
            url: href,
            success: function () {
                $('#deleteNoteModal').modal('hide');
                location.reload();
            }
        });
    });

    // ************************ SAVE NOTE CANCEL BUTTON ************************
    $('#addNoteModal').find('.exitBtn').on('click', function () {
        $('#addNoteModal').modal('hide');
        resetValidation();
    });

    // ************************ UPDATE NOTE CANCEL BUTTON ************************
    $('#editNoteModal').find('.exitBtn').on('click', function () {
        $('#editNoteModal').modal('hide');
        resetValidation();
    });

    // ************************ DELETE NOTE CANCEL BUTTON ************************
    $('#deleteNoteModal').find('.exitBtn').on('click', function () {
       $('#deleteNoteModal').modal('hide');
    });
});

function getNote(data) {
    const url = '/users/' + $('#userGuid').text() + '/notes/' + data.guid;

    return "<div class='card bg-light shadow mb-5'>\n" +
        "   <div class='card-header'>\n" +
        "      <h5 class='mb-0'>" + data.title + "</h5>\n" +
        "   </div>\n" +
        "   <div class='card-body'>" + data.body + "</div>\n" +
        "   <div class='card-footer padding-bottom-0'>\n" +
        "      <div class='row mb-0'>\n" +
        "         <div class='col-md timestamp'>\n" +
        "             " + data.createdAt + "\n" +
        "         </div>\n" +
        "         <div class='col-md text-right'>\n" +
        "            <button class='btn btn-sm btn-dark editNoteBtn' href='" + url +"'><i\n" +
        "               class='fa fa-edit'></i></button>\n" +
        "            <button class='btn btn-sm btn-dark ml-3 deleteNoteBtn' href='" + url +"'><i\n" +
        "               class='fa fa-trash-alt'></i></button>\n" +
        "         </div>\n" +
        "      </div>\n" +
        "   </div>\n" +
        "</div>";
}