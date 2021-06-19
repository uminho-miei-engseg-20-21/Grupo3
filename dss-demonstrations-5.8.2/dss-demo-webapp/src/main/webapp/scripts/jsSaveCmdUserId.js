$(document).ready(() => {
    const $userId = $("#userId");
    const userId = localStorage.getItem("userId");

    if(userId) {
        $userId.val(userId);
    } else {
        $userId.val("+351 ");
    }

    $userId.parents("form").submit(e => {
        localStorage.setItem("userId", $userId.val());
    });
});
