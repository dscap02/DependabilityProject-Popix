document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#productForm'); // Assicurati che il form abbia l'id 'productForm'

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Blocca il comportamento predefinito del form

        const id = document.getElementById('idProduct').value.trim();
        const name = document.getElementById('name').value.trim();
        const description = document.getElementById('description').value.trim();
        const cost = document.getElementById('price').value.trim();
        const piecesInStock = document.getElementById('qty').value.trim();
        const brand = document.getElementById('brand').value.trim();
        const figure = document.getElementById('figure').value.trim();
        const imgFile = document.getElementById('img_src').files[0];

        // Validazioni
        if (!/^[A-Za-z0-9]{1,5}$/.test(id)) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'L\'ID deve essere composto da massimo 5 caratteri alfanumerici.',
            });
            return;
        }
        if (name.length === 0 || name.length > 100) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'Il nome deve essere compreso tra 1 e 100 caratteri.',
            });
            return;
        }
        if (description.length === 0) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'La descrizione non può essere vuota.',
            });
            return;
        }
        if (Number.isNaN(cost) || cost <= 0) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'Il prezzo deve essere un numero maggiore di zero.',
            });
            return;
        }
        if (Number.isNaN(piecesInStock) || piecesInStock < 0) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'La quantità deve essere un numero maggiore o uguale a zero.',
            });
            return;
        }
        if (brand.length === 0 || brand.length > 100) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'Il brand deve essere compreso tra 1 e 100 caratteri.',
            });
            return;
        }
        if (figure.length === 0 || figure.length > 100) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'Il personaggio deve essere compreso tra 1 e 100 caratteri.',
            });
            return;
        }
        if (!imgFile) {
            Swal.fire({
                icon: 'error',
                title: 'Errore di validazione',
                text: 'Devi caricare un\'immagine.',
            });
            return;
        } else {
            const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
            if (!allowedTypes.includes(imgFile.type)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Errore di validazione',
                    text: 'Il file immagine deve essere nei formati JPG, PNG o GIF.',
                });
                return;
            }
            if (imgFile.size > 2 * 1024 * 1024) { // 2 MB
                Swal.fire({
                    icon: 'error',
                    title: 'Errore di validazione',
                    text: 'Il file immagine non deve superare i 2MB.',
                });
                return;
            }
        }

        // Preparazione dei dati per l'invio
        const formData = new FormData(form);

        // Invio dei dati alla servlet
        fetch(form.action, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Successo!',
                        text: data.message,
                        showConfirmButton: false,
                        timer: 1500
                    }).then(() => location.reload());
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Errore!',
                        text: data.message,
                        showConfirmButton: true
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Errore di sistema',
                    text: 'Si è verificato un errore durante l\'invio dei dati.',
                    showConfirmButton: true
                });
            });
    });
});
