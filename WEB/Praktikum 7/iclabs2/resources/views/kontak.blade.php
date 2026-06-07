@extends('layouts.app')

@section('title', 'Kontak')

@section('content')
    <h1 class="text-center">Kontak Kami</h1>
    <p class="text-center">
        Anda dapat menghubungi kami melalui email di web resmi kami 
        dan juga melalui sosial media kami.
    </p>
    <div class="container row">
        <form class="col-lg-6 col-sm-12" id="contact-form">
            <div class="mb-3">
                <label for="name" class="form-label">Your Name</label>
                <input type="text" class="form-control" id="name" required>
            </div>
            <div class="mb-3">
                <label for="phone" class="form-label">Your Phone</label>
                <input type="text" class="form-control" id="phone" required>
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">Eemail address</label>
                <input type="email" class="form-control" id="email" required>
            </div>
            <div class="mb-3">
                <label for="message" class="form-label">Message</label>
                <textarea class="form-control" id="message" rows="2"  required></textarea>
            </div>
            <div class="d-grid">
            <button class="btn btn-primary" type="button" id="submit-button">Submit</button>
            </div>
        </form>
        <div class="col-lg-6 col-sm-12 shadow-lg-3 mb-5 rounded">
            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3973.7984857937813!2d119.4463468113044!3d-5.136124051933115!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x2dbefd3165008369%3A0x7af75b8baf265f2b!2sFakultas%20Ilmu%20Komputer%20UMI!5e0!3m2!1sen!2sid!4v1744002738550!5m2!1sen!2sid" width="600" height="400" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
        </div>
    </div>

    <!-- Notifikasi Pop-Up -->
    <div id="notification" class="bg-success text-light px-4 py-2 shadow" style="display: none; bottom: 20px; right: 20px; border-radius: 5px;">
        Terima kasih atas Pesanya!
    </div>
    <script>
        document.getElementById('submit-button').addEventListener('click', function() {
            const notification = document.getElementById('notification');
            notification.style.display = 'block';
            setTimeout(() => {
                notification.style.display = 'none';
            }, 5000); 
            document.getElementById('contact-form').reset();
        });
    </script>
@endsection