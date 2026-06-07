@extends('layouts.app')
@section('content') 
    <div class="container-fluid"> 
        <h2>Menu List</h2> 
        <a href="{{ route('menus.create') }}" class="btn btn-dark">Create New Menu</a> 
        <table class="table table-bordered mt-3 shadow"> 
            <thead> 
                <tr> 
                    <th>Id Minuman</th> 
                    <th>Name</th> 
                    <th>Description</th> 
                    <th>Price</th> 
                    <th>Action</th> 
                </tr> 
            </thead> 
            <tbody> 
                @foreach ($menus as $index => $menu) 
                    <tr> 
                        <td>{{ $menu->id }}</td> 
                        <td>{{ $menu->name }}</td> 
                        <td>{{ $menu->description }}</td> 
                        <td>Rp. {{ number_format($menu->price, 0, ',', '.') }}</td> 
                        <td> 
                            <form action="{{ route('menus.destroy', $menu->id) }}" method="POST"> 
                                <a class="btn btn-dark" href="{{ route('menus.show', $menu->id) }}">Show</a> 
                                <a class="btn btn-outline-dark" href="{{ route('menus.edit', $menu->id) }}">Edit</a> 
                                @csrf 
                                @method('DELETE') 
                                <button type="submit" class="btn btn-danger">Delete</button> 
                            </form> 
                        </td> 
                    </tr> 
                @endforeach 
            </tbody> 
        </table> 
    </div> 
@endsection