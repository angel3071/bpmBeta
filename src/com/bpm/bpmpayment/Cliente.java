package com.bpm.bpmpayment;

public class Cliente extends ElementoGrid {
	private String correo;
	
	public Cliente(String nombre, int pic, String correo) {
		super(nombre, pic);
		this.correo = correo;
	}
	
	public String getNombre() {
		return super.nombre;
	}
	
	public String getCorreo() {
		return this.correo;
	}
}