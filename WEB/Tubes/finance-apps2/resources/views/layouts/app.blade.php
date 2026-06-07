<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title') - Manajemen Keuangan</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        main {
            flex: 1;
        }
        
        .sidebar {
            min-height: calc(100vh - 56px);
            background-color: #343a40;
            color: white;
        }
        
        .sidebar a {
            color: #ced4da;
            text-decoration: none;
        }
        
        .sidebar a:hover {
            color: #fff;
        }
        
        .sidebar .active {
            background-color: #495057;
            color: #fff;
        }
        
        @media (max-width: 768px) {
            .sidebar {
                min-height: auto;
            }
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="{{ route('dashboard') }}">
                <i class="fas fa-wallet me-2"></i>
                Manajemen Keuangan
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    @guest
                        <li class="nav-item">
                            <a class="nav-link" href="{{ route('login') }}">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="{{ route('register') }}">Register</a>
                        </li>
                    @else
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-circle me-1"></i> {{ Auth::user()->name }}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                                <li><a class="dropdown-item" href="#">Profil</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item" href="#" onclick="event.preventDefault(); document.getElementById('logout-form').submit();">
                                        Logout
                                    </a>
                                    <form id="logout-form" action="{{ route('logout') }}" method="POST" class="d-none">
                                        @csrf
                                    </form>
                                </li>
                            </ul>
                        </li>
                    @endguest
                </ul>
            </div>
        </div>
    </nav>

    <!-- Content -->
    <div class="container-fluid p-0">
        <div class="row g-0">
            @auth
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 d-md-block sidebar collapse" id="sidebarMenu">
                    <div class="d-flex flex-column p-3">
                        <ul class="nav nav-pills flex-column mb-auto">
                            <li class="nav-item">
                                <a href="{{ route('dashboard') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('dashboard') ? 'active' : '' }}">
                                    <i class="fas fa-tachometer-alt me-2"></i>
                                    Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="{{ route('transactions.index') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('transactions.*') ? 'active' : '' }}">
                                    <i class="fas fa-exchange-alt me-2"></i>
                                    Transaksi
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="{{ route('accounts.index') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('accounts.*') ? 'active' : '' }}">
                                    <i class="fas fa-piggy-bank me-2"></i>
                                    Akun
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="{{ route('categories.index') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('categories.*') ? 'active' : '' }}">
                                    <i class="fas fa-tags me-2"></i>
                                    Kategori
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="{{ route('budgets.index') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('budgets.*') ? 'active' : '' }}">
                                    <i class="fas fa-chart-pie me-2"></i>
                                    Anggaran
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="{{ route('reports.index') }}" class="nav-link d-flex align-items-center {{ request()->routeIs('reports.*') ? 'active' : '' }}">
                                    <i class="fas fa-chart-bar me-2"></i>
                                    Laporan
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
                
                <!-- Main Content -->
                <div class="col-md-9 col-lg-10">
                    <main>
                        @yield('content')
                    </main>
                </div>
            @else
                <!-- Full Width Content for Guest -->
                <div class="col-12">
                    <main>
                        @yield('content')
                    </main>
                </div>
            @endauth
        </div>
    </div>
    
    <!-- Footer -->
    <footer class="bg-dark text-light py-3 mt-auto">
        <div class="container text-center">
            <p class="mb-0">&copy; {{ date('Y') }} Manajemen Keuangan. All rights reserved.</p>
        </div>
    </footer>
    
    <!-- Bootstrap 5 JavaScript Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Additional Scripts -->
    @yield('scripts')
</body>
</html>