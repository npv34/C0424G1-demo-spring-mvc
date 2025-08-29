$(document).ready(function () {
    // ma nguon jquery

    $(".image-user").click(function () {
        const urlImage = $(this).attr("src")
        $("#image-show").attr("src", urlImage);
        $("#myModal").css("display", "block");
    })

    $(".close-modal-image-show").click(function (){
        $("#myModal").css("display", "none");
    })

    $("#input-search-user").keyup(function () {
        const value = $(this).val();
        $.ajax({
            url: "/users/search",
            method: "GET",
            data: {
                keyword: value
            },
            success: function( result) {
                console.log(result)
                showUserSearch(result.users)
            },
            error: function (err) {

            }
        });
    })

    const showUserSearch = (users) => {
        let html = "";
        users.forEach((item, index) => {
            html += '<tr>';
            html += `<td>${index+1}</td>`
            html += `<td>${item.username}</td>`
            html += `<td><img width="150" src="/resources/uploads/${item.imageUrl}"></td>`
            html += `<td>${item.email}</td>`
            html += `<td>${item.phone}</td>`
            html += `<td>${item.departmentName}</td>`
            html += `
                <td>
                              <a class="btn btn-danger" onclick="return confirm('Are you sure')" th:href="@{/users/{id}/delete(id=${item.id})}">Delete</a>
                              <a class="btn btn-primary" th:href="@{/users/{id}/edit(id=${item.id})}">Edit</a>
                          </td>
            
            `
            html += '<tr>';
        })

        $("#list-user").html(html)
    }
})