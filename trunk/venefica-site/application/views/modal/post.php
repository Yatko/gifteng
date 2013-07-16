<script langauge="javascript">
    function initPostModal() {
        init_select();
        init_checkbox();
        
        hide_file($('#ad_image'));
        open_file($('.ge-post-image-btn'));
        open_file($('.ge-post-image-img'));
        attach_file($('.ge-post-image-btn'));
        
        $('#member_post_form').on('submit', function(e) {
            e.preventDefault();

            var formData = new FormData($("#member_post_form").get(0));

            $.ajax({
                type: "POST",
                url: '<?=base_url()?>post/member?modal',
                dataType: 'html',
                cache: false,
                data: formData,
                processData: false,
                contentType: false
            }).done(function(response) {
                $('#postContainer > .modal-body').html(response);
                
                //should reattach events for the new content
                initPostModal();
            }).fail(function(data) {
                //TODO
            });
        });
    }
    
    $(function() {
        $('#postContainer').on('shown', function() {
            initPostModal();
        });
        
        $('#postContainer').on('hidden', function() {
            $(this).removeData("modal");
            $('#postContainer > .modal-body').html('');
        });
    });
</script>

<div id="postContainer" class="modal hide fade" data-remote="<?= base_url() ?>post?modal" data-backdrop="static" data-keyboard="false">
    <div class="modal-body"></div>
</div>
