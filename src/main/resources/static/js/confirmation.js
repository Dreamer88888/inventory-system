function confirmAdd(event) {
    event.preventDefault();
    let targetEndpoint = ${event.currentTarget}.data("href");

    let text = "Apakah Anda yakin ingin menambahkan data?";
    if (confirm(text) == true) {
        window.location.href = targetEndpoint;
    }
}