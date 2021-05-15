
$('input[name="signaturePackaging"]:radio').attr("disabled", true);

$('input[name="containerType"]:radio').filter('[value="none"]').attr('checked', true);

$('#selectSignatureLevel').empty();

function isAsicContainer() {
	var container = $('input[name=containerType]:checked').val();
	return 'none' != container;
}

$('input[name="containerType"]:radio').change(
    function() {
        var asicValue = this.value;

        $('input[name="signatureForm"]:radio').prop("checked", false);
        $('input[name="signatureForm"]:radio').attr("disabled", true);

        $('input[name="signaturePackaging"]:radio').prop("checked", false);
        $('input[name="signaturePackaging"]:radio').attr("disabled", true);

        $('#selectSignatureLevel').empty();

        if ('none' == asicValue) {
            $("#formCAdES").attr("disabled", false);
            $("#formPAdES").attr("disabled", false);
            $("#formXAdES").attr("disabled", false);
            $("#formJAdES").attr("disabled", false);

        } else {
            $("#formCAdES").attr("disabled", false);
            $("#formXAdES").attr("disabled", false);

            $("#signaturePackaging-DETACHED").attr("disabled", false);
            $("#signaturePackaging-DETACHED").prop("checked", true);

        }
    });


$('input[name="signatureForm"]:radio').change(
    function() {

        $('#selectSignatureLevel').empty();

        var process = $('#process').val();

        if (!isAsicContainer()) {
            $('input[name="signaturePackaging"]:radio').attr("disabled", true).prop("checked", false);

            $.ajax({
                type : "GET",
                url : "data/packagingsByForm?form=" + this.value,
                dataType : "json",
                error : function(msg) {
                    alert("Error !: " + msg);
                },
                success : function(data) {
                    $.each(data, function(idx) {
                        $('#signaturePackaging-' + data[idx]).attr("disabled", false);
                    });
                    if (data.length == 1) {
                        $('#signaturePackaging-' + data[0]).prop("checked", true);
                    }
                }
            });
        }

        $.ajax({
            type : "GET",
            url : "data/levelsByForm?form=" + this.value+"&process="+process,
            dataType : "json",
            error : function(msg) {
                alert("Error !: " + msg);
            },
            success : function(data) {
                $.each(data, function(idx) {
                    $('#selectSignatureLevel').append($('<option>', {
                        value: data[idx],
                        text: data[idx].replace(/_/g, "-")
                    }));
                });
            }
        });

        getDigestAlgorithms(this.value);

    });
