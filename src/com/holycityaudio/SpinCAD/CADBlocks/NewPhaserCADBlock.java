/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 */

package com.holycityaudio.SpinCAD.CADBlocks;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class NewPhaserCADBlock extends ModulationCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;
	int temp, temp1, phase, stages;


	public NewPhaserCADBlock(int x, int y) {
		super(x, y);
		stages = 8;
		addControlInputPin(this, "Phase");
		// TODO Auto-generated constructor stub
		setName("New Phaser");
	}

		public void generateCode(SpinFXBlock sfxb) {

		//				equ	mono	reg0
		int MONO = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		sfxb.comment(getName());


		if(p != null) {
			MONO = p.getRegister();
			//				equ	phase	reg5
			p = this.getPin("Phase").getPinConnection();

			if(p != null) {
				phase = p.getRegister();
			}
			else {
				phase = sfxb.allocateReg();
				sfxb.scaleOffset(0.0, 0.5);
				sfxb.writeRegister(phase, 0.0);
			}
				
			//				equ	pout	reg6
			int POUT = sfxb.allocateReg();
			//				equ	p1	reg7

			int p1 = sfxb.allocateReg();
			//				equ	p2	reg8
			int p2 = sfxb.allocateReg();

			int p3 = 0;
			int p4 = 0;
			if(stages > 2) {
				p3 = sfxb.allocateReg();
				p4 = sfxb.allocateReg();
			}

			int p5 = sfxb.allocateReg();
			int p6 = sfxb.allocateReg();
			if(stages > 4) {
				p5 = sfxb.allocateReg();
				p6 = sfxb.allocateReg();
			}

			int p7 = 0;
			int p8 = 0;
			if(stages > 6) {
				p7 = sfxb.allocateReg();
				p8 = sfxb.allocateReg();
			}

			temp = sfxb.allocateReg();
			temp1 = sfxb.allocateReg();

			int BYPASS = sfxb.allocateReg();

			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO(1, 0, 32767);

			p = this.getPin("Phase").getPinConnection();

// beginning of phase shifter proper
			sfxb.readRegister(p1, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(MONO, 1.0/64);
			sfxb.writeRegister(p1, -1);
			sfxb.mulx(phase);

			PhaseShiftStage(sfxb ,p2);
			if(stages > 2) {
				PhaseShiftStage(sfxb ,p3);
				PhaseShiftStage(sfxb ,p4);
				sfxb.scaleOffset(-2.0, 0.0);
				sfxb.scaleOffset(-2.0, 0.0);
			}
			if (stages > 4) {
				PhaseShiftStage(sfxb ,p5);
				PhaseShiftStage(sfxb ,p6);
				sfxb.scaleOffset(-2.0, 0.0);
				sfxb.scaleOffset(-2.0, 0.0);
			}
			if(stages > 6) {
				PhaseShiftStage(sfxb ,p7);
				PhaseShiftStage(sfxb ,p8);
				sfxb.scaleOffset(-2.0, 0.0);
				sfxb.scaleOffset(-2.0, 0.0);
			}
			sfxb.readRegister(temp, 1);

			//					mulx	bypass
//			sfxb.mulx(BYPASS);
			//					rdax	mono,1
//			sfxb.readRegister(MONO, 1);
			//					wrax	pout,1
			sfxb.writeRegister(POUT, 0);

			// last instruction clears accumulator
			p = this.getPin("Audio Output 1");
			p.setRegister(POUT);
		}
		System.out.println("New Phaser code gen!");
	}

	private void PhaseShiftStage(SpinFXBlock sfxb, int register) {
		//					rdax	temp,1
		sfxb.readRegister(temp, 1);
		//					wrax	temp1,0
		sfxb.writeRegister(temp1, 0);
		//					rdax	p6,1
		sfxb.readRegister(register, 1);
		//					wrax	temp,1
		sfxb.writeRegister(temp, 1);
		//					mulx	phase
		sfxb.mulx(phase);
		//					rdax	temp1,1
		sfxb.readRegister(temp1, 1);
		//					wrax	p6,-1
		sfxb.writeRegister(register, -1);
		//					mulx	phase
		sfxb.mulx(phase);
	}

	public int getStages() {
		return stages;
	}

	public void setStages(int stages) {
		this.stages = stages;
	}
	
}